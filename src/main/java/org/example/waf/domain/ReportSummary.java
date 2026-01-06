package org.example.waf.domain;



public class ReportSummary {
    private int totalAttacks;
    private int blockedAttacks;
    private double blockRate;
    private int highRiskAttacks;

    // Getter & Setter
    public int getTotalAttacks() { return totalAttacks; }
    public void setTotalAttacks(int totalAttacks) { this.totalAttacks = totalAttacks; }
    public int getBlockedAttacks() { return blockedAttacks; }
    public void setBlockedAttacks(int blockedAttacks) { this.blockedAttacks = blockedAttacks; }
    public double getBlockRate() { return blockRate; }
    public void setBlockRate(double blockRate) { this.blockRate = blockRate; }
    public int getHighRiskAttacks() { return highRiskAttacks; }
    public void setHighRiskAttacks(int highRiskAttacks) { this.highRiskAttacks = highRiskAttacks; }
}
