package org.example.waf.waf;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waf")
public class WafController {
    private final WafService service;

    public WafController(WafService service) {
        this.service = service;
    }

    @GetMapping("/enabled")
    public Map<String, Object> getEnabled() {
        Map<String, Object> m = new HashMap<>();
        m.put("enabled", service.isEnabled());
        return m;
    }

    @PostMapping("/rules:reload")
    public Map<String, Object> reload() throws Exception {
        boolean ok = service.reloadFromFile();
        Map<String, Object> m = new HashMap<>();
        m.put("reloaded", ok);
        return m;
    }

    @PostMapping("/rules:save")
    public Map<String, Object> save() throws Exception {
        boolean ok = service.saveToFile();
        Map<String, Object> m = new HashMap<>();
        m.put("saved", ok);
        return m;
    }

    @PostMapping("/enabled")
    public Map<String, Object> setEnabled(@RequestParam("enabled") boolean enabled) {
        service.setEnabled(enabled);
        Map<String, Object> m = new HashMap<>();
        m.put("enabled", service.isEnabled());
        return m;
    }

    @GetMapping("/rules")
    public List<Rule> listRules() {
        return service.listRules();
    }

    @PostMapping("/rules")
    public Rule create(@RequestBody Rule rule) {
        return service.createRule(rule);
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<Rule> update(@PathVariable String id, @RequestBody Rule rule) {
        Rule r = service.updateRule(id, rule);
        return r == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }

    @DeleteMapping("/rules/{id}")
    public Map<String, Object> delete(@PathVariable String id) {
        boolean ok = service.deleteRule(id);
        Map<String, Object> m = new HashMap<>();
        m.put("deleted", ok);
        return m;
    }

    @PostMapping("/test")
    public Map<String, Object> test(@RequestParam(value = "query", required = false) String query,
                                    @RequestParam(value = "path", required = false) String path) {
        Map<String, Object> m = new HashMap<>();
        boolean blocked = false;
        for (Rule r : service.listRules()) {
            if (!r.isEnabled()) continue;
            String patt = r.getPattern() == null ? "" : r.getPattern().toLowerCase();
            if ("query".equals(r.getScope()) && query != null && query.toLowerCase().contains(patt) && "block".equalsIgnoreCase(r.getAction())) {
                blocked = true; break;
            }
            if ("path".equals(r.getScope()) && path != null && path.toLowerCase().contains(patt) && "block".equalsIgnoreCase(r.getAction())) {
                blocked = true; break;
            }
        }
        m.put("blocked", blocked);
        return m;
    }
}


