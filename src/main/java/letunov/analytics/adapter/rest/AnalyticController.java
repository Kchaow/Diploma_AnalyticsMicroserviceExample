package letunov.analytics.adapter.rest;

import letunov.contract.ContractProvider;
import letunov.contracts.UpdateInventoryStateContract;
import letunov.contracts.dto.ItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ContractProvider
public class AnalyticController implements UpdateInventoryStateContract {
    @Override
    @PutMapping("/analytics/inventory")
    public ResponseEntity<Void> updateInventoryState(@RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().build();
    }
}
