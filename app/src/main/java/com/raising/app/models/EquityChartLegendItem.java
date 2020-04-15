package com.raising.app.models;

import androidx.lifecycle.ViewModelProviders;

import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.viewModels.ResourcesViewModel;

import lombok.Data;

@Data
public class EquityChartLegendItem {
   private String title;
   private String typeString;
   private int color;
   private float equityShare;

    public EquityChartLegendItem(int color, String typeString, String title,
                                 float equityShare) {
        this.color = color;
        this.typeString = typeString;
        this.title = title;
        this.equityShare = equityShare;
    }

    public String getEquityShareString() {
        return equityShare + "%";
    }
}
