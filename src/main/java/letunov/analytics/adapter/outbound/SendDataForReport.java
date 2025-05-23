package letunov.analytics.adapter.outbound;

import letunov.contract.ContractConsumer;
import letunov.contracts.SendDataForReportContract;
import letunov.contracts.dto.ReportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@ContractConsumer(serviceName = "reports")
public class SendDataForReport implements SendDataForReportContract {
    @Value("${integration.reports-url}")
    private String reportsUrl;

    @Override
    public ResponseEntity<Void> sendDataForReport(ReportDto dto) {
        return WebClient.create(reportsUrl)
                .post()
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
