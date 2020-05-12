package com.raising.app.util.matchingCriteriaComponent;

import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.raising.app.models.MatchingCriteriaItem;
import com.raising.app.models.Model;

import java.util.ArrayList;

public class MatchingCriteriaComponent implements LifecycleObserver {
    private final String TAG = "MatchingCriteriaComponent";
    private RecyclerView recyclerView;
    private MatchingCriteriaAdapter itemAdapter;
    private boolean singleSelect = false;
    private ArrayList<MatchingCriteriaItem> items = new ArrayList<>();

    /**
     * Create a new Matching Criteria Component
     * @param recyclerView
     * @param items
     * @param singleSelect
     */
    public MatchingCriteriaComponent(RecyclerView recyclerView, ArrayList<? extends Model> items, boolean singleSelect,
                                     MatchingCriteriaAdapter.OnItemClickListener clickListener) {
        items.forEach(item -> {
            this.items.add(new MatchingCriteriaItem(item.getId(), item.getName(), item.getImage()));
        });

        itemAdapter = new MatchingCriteriaAdapter(recyclerView.getContext(), this.items, singleSelect, false);
        this.recyclerView = recyclerView;
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(recyclerView.getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(itemAdapter);
        this.singleSelect = singleSelect;
        this.itemAdapter.setOnItemClickListener(clickListener);
    }

    /**
     * Create a new Matching Criteria Component
     * @param recyclerView
     * @param items
     * @param singleSelect
     * @param isLabelsLayout 
     */
    public MatchingCriteriaComponent(RecyclerView recyclerView, ArrayList<? extends Model> items, boolean singleSelect,
                                     MatchingCriteriaAdapter.OnItemClickListener clickListener, boolean isLabelsLayout) {
        items.forEach(item -> {
            this.items.add(new MatchingCriteriaItem(item.getId(), item.getName(), item.getImage()));
        });

        itemAdapter = new MatchingCriteriaAdapter(recyclerView.getContext(), this.items, singleSelect, isLabelsLayout);

        this.recyclerView = recyclerView;
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(recyclerView.getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        layoutManager.setAlignItems(AlignItems.BASELINE);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(itemAdapter);
        this.singleSelect = singleSelect;
        this.itemAdapter.setOnItemClickListener(clickListener);
    }

    /**
     * Get all selected items
     * @return array list of selected models
     */
    public ArrayList<Long> getSelected() {
        ArrayList<Long> result = new ArrayList<>();
        for(int i = 0; i < items.size(); ++i) {
            if(items.get(i).isChecked()) {
                result.add(items.get(i).getId());
            }
        }
        return result;
    }

    /**
     * Get single selected item
     * @return
     */
    public long getSingleSelected() {
        for(int i = 0; i < items.size(); ++i) {
            if(items.get(i).isChecked()) {
                return(items.get(i).getId());
            }
        }
        return -1;
    }

    /**
     * Set item with given id as checked
     * @param id id of item
     */
    public void setChecked(long id) {
        for(int i = 0; i < items.size(); ++i) {
            if(items.get(i).getId() == id) {
                items.get(i).setChecked(true);
                itemAdapter.notifyDataSetChanged();
                return;
            }
        }
    }
}
