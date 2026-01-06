package org.example.waf.waf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WafConfigState {
    private boolean enabled = true;
    private final List<Rule> rules = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public void replaceRules(List<Rule> newRules) {
        rules.clear();
        if (newRules != null) {
            rules.addAll(newRules);
        }
        rules.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        rules.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }

    public boolean removeRuleById(String id) {
        return rules.removeIf(r -> r.getId().equals(id));
    }
}



