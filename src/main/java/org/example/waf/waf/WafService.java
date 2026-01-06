package org.example.waf.waf;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WafService {
    private final WafConfigState state = new WafConfigState();
    private final Map<String, Rule> ruleById = new ConcurrentHashMap<>();
    private final RuleFileStore fileStore;

    public WafService(RuleFileStore fileStore) {
        this.fileStore = fileStore;
        // 优先从文件加载；否则落回内置默认规则
        boolean loaded = reloadFromFileSilently();
        if (!loaded) {
            loadDefaults();
        }
    }

    private void loadDefaults() {
        String[] defaults = new String[]{"union","select","insert","delete","drop","script","alert","../","..\\"};
        int priority = 100;
        for (String p : defaults) {
            Rule r = new Rule();
            r.setId(UUID.randomUUID().toString());
            r.setPattern(p);
            r.setAction("block");
            r.setScope("query");
            r.setEnabled(true);
            r.setPriority(priority++);
            ruleById.put(r.getId(), r);
            state.addRule(r);
        }
    }

    private boolean reloadFromFileSilently() {
        try {
            return reloadFromFile();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean reloadFromFile() throws IOException {
        RuleFileStore.PersistedConfig cfg = fileStore.load();
        if (cfg == null) {
            return false;
        }
        state.setEnabled(cfg.enabled);
        ruleById.clear();
        if (cfg.rules != null) {
            for (Rule r : cfg.rules) {
                if (r.getId() == null || r.getId().isEmpty()) {
                    r.setId(UUID.randomUUID().toString());
                }
                ruleById.put(r.getId(), r);
            }
        }
        state.replaceRules(new ArrayList<>(ruleById.values()));
        return true;
    }

    private void saveSilently() {
        try {
            fileStore.save(state.isEnabled(), state.getRules());
        } catch (IOException ignored) {
        }
    }

    public boolean isEnabled() {
        return state.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        state.setEnabled(enabled);
        saveSilently();
    }

    public List<Rule> listRules() {
        return state.getRules();
    }

    public Rule createRule(Rule rule) {
        if (rule.getId() == null || rule.getId().isEmpty()) {
            rule.setId(UUID.randomUUID().toString());
        }
        if (rule.getPriority() == 0) {
            rule.setPriority(1000);
        }
        ruleById.put(rule.getId(), rule);
        state.addRule(rule);
        saveSilently();
        return rule;
    }

    public Rule updateRule(String id, Rule update) {
        Rule existing = ruleById.get(id);
        if (existing == null) {
            return null;
        }
        if (update.getPattern() != null) existing.setPattern(update.getPattern());
        if (update.getAction() != null) existing.setAction(update.getAction());
        if (update.getScope() != null) existing.setScope(update.getScope());
        existing.setEnabled(update.isEnabled());
        if (update.getPriority() != 0) existing.setPriority(update.getPriority());
        state.replaceRules(new ArrayList<>(ruleById.values()));
        saveSilently();
        return existing;
    }

    public boolean deleteRule(String id) {
        Rule removed = ruleById.remove(id);
        boolean ok = removed != null && state.removeRuleById(id);
        if (ok) {
            saveSilently();
        }
        return ok;
    }

    public boolean saveToFile() throws IOException {
        fileStore.save(state.isEnabled(), state.getRules());
        return true;
    }
}


