package za.co.rubhub.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailService {
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    public void sendPayoutReport(PayoutProcessingResult result) {
        try {
            Email from = new Email(fromEmail);
            String subject = "RubHub Therapist Payout Report - " + result.getProcessingDate().toLocalDate();
            Email to = new Email("finance@rubhub.com"); // Finance team email
            
            String htmlContent = buildPayoutReportHtml(result);
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Payout report email sent successfully");
            } else {
                log.error("Failed to send payout report email: {}", response.getBody());
            }
            
        } catch (IOException e) {
            log.error("Error sending payout report email: {}", e.getMessage(), e);
        }
    }
    
    private String buildPayoutReportHtml(PayoutProcessingResult result) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; }\n" +
            "        .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }\n" +
            "        .summary { margin: 20px; padding: 15px; border: 1px solid #ddd; }\n" +
            "        .stats { display: flex; justify-content: space-between; margin: 20px 0; }\n" +
            "        .stat-card { background: #f5f5f5; padding: 15px; border-radius: 5px; flex: 1; margin: 0 10px; text-align: center; }\n" +
            "        .therapist-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }\n" +
            "        .therapist-table th, .therapist-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
            "        .therapist-table th { background-color: #f2f2f2; }\n" +
            "        .success { color: #4CAF50; }\n" +
            "        .warning { color: #ff9800; }\n" +
            "        .error { color: #f44336; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"header\">\n" +
            "        <h1>RubHub Therapist Payout Report</h1>\n" +
            "        <p>Processing Date: %s</p>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class=\"summary\">\n" +
            "        <h2>Processing Summary</h2>\n" +
            "        <div class=\"stats\">\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>Total Bookings</h3>\n" +
            "                <p class=\"success\">%d</p>\n" +
            "            </div>\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>Successful Payouts</h3>\n" +
            "                <p class=\"success\">%d</p>\n" +
            "            </div>\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>Failed Payouts</h3>\n" +
            "                <p class=\"%s\">%d</p>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class=\"stats\">\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>Total Processed</h3>\n" +
            "                <p>$%.2f</p>\n" +
            "            </div>\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>Therapist Payouts</h3>\n" +
            "                <p>$%.2f</p>\n" +
            "            </div>\n" +
            "            <div class=\"stat-card\">\n" +
            "                <h3>RubHub Fees</h3>\n" +
            "                <p>$%.2f</p>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class=\"therapist-summary\">\n" +
            "        <h2>Therapist Breakdown</h2>\n" +
            "        <table class=\"therapist-table\">\n" +
            "            <thead>\n" +
            "                <tr>\n" +
            "                    <th>Therapist ID</th>\n" +
            "                    <th>Bookings</th>\n" +
            "                    <th>Therapist Payout</th>\n" +
            "                    <th>RubHub Fees</th>\n" +
            "                </tr>\n" +
            "            </thead>\n" +
            "            <tbody>\n" +
            "                %s\n" +
            "            </tbody>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            result.getProcessingDate().toString(),
            result.getTotalBookings(),
            result.getSuccessfulPayouts(),
            result.getFailedPayouts() > 0 ? "error" : "success",
            result.getFailedPayouts(),
            result.getTotalProcessedAmount(),
            result.getTotalTherapistPayouts(),
            result.getTotalRubhubFees(),
            buildTherapistTableRows(result)
        );
    }
    
    private String buildTherapistTableRows(PayoutProcessingResult result) {
        StringBuilder sb = new StringBuilder();
        for (PayoutProcessingResult.TherapistSummary summary : result.getTherapistSummaries()) {
            sb.append(
                "<tr>\n" +
                "    <td>" + summary.getBookingsCount() + "</td>\n" +
                "    <td>$" + String.format("%.2f", summary.getTherapistAmount()) + "</td>\n" +
                "    <td>$" + String.format("%.2f", summary.getRubhubFees()) + "</td>\n" +
                "</tr>\n"
            );
        }
        return sb.toString();
    }
    
    // Alternative method using String.format for therapist rows
    private String buildTherapistTableRowsWithFormat(PayoutProcessingResult result) {
        StringBuilder sb = new StringBuilder();
        for (PayoutProcessingResult.TherapistSummary summary : result.getTherapistSummaries()) {
            String row = String.format(
                "<tr>\n" +
                "    <td>%s</td>\n" +
                "    <td>%d</td>\n" +
                "    <td>$%.2f</td>\n" +
                "    <td>$%.2f</td>\n" +
                "</tr>\n",
                summary.getBookingsCount(),
                summary.getTherapistAmount(),
                summary.getRubhubFees()
            );
            sb.append(row);
        }
        return sb.toString();
    }
}