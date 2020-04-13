package com.raising.app.models;

import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.util.ResourcesManager;

import lombok.Data;

@Data
public class ShareholderEquityChartLegendItem {
   private String title;
   private String typeString;
   private int color;
   private float equityShare;

    public ShareholderEquityChartLegendItem(int color, Shareholder shareholder) {
        title = shareholder.getTitle();
        equityShare = shareholder.getEquityShare();
        if(shareholder.isPrivateShareholder()) {
            typeString = ResourcesManager.getInvestorType(shareholder.getInvestorTypeId()).getName();
        } else {
            typeString = ResourcesManager.getCorporateBody(shareholder.getCorporateBodyId()).getName();
        }
        this.color = color;
    }

    public String getEquityShareString() {
        return equityShare + "%";
    }
}
