package com.raising.app.util.customPicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.util.customPicker.listeners.OnItemClickListener;
import com.raising.app.R;

import java.util.List;

public class CustomPickerAdapter extends
    RecyclerView.Adapter<CustomPickerAdapter.ViewHolder> {

  private OnItemClickListener listener;
  private List<? extends PickerItem> items;
  private Context context;
  private int textColor;
  private boolean multiSelect;

  public CustomPickerAdapter(Context context, List<? extends PickerItem> items,
                             OnItemClickListener listener, int textColor, boolean multiSelect) {
    this.context = context;
    this.items = items;
    this.listener = listener;
    this.textColor = textColor;
    this.multiSelect = multiSelect;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country,
            parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final PickerItem item = items.get(position);
    holder.itemNameText.setText(item.getName());

    if(item.getParentId() == item.getId())
      holder.itemNameText.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
    else
      holder.itemNameText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

    holder.itemNameText.setTextColor(textColor == 0 ? Color.BLACK : textColor);
    if(!multiSelect)
      holder.checkBox.setVisibility(View.GONE);

    holder.checkBox.setChecked(item.isChecked());

    if(item.getParentId() == item.getId() && item.isChecked()) {
      for(PickerItem it : items) {
        if(it.getParentId() == item.getId()) {
          it.setChecked(true);
        }
      }
    }

    holder.rootView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(item.getParentId() == item.getId()) {
          item.setChecked(!item.isChecked());
          for(PickerItem it : items) {
            if(it.getParentId() == item.getId()) {
              it.setChecked(item.isChecked());
              notifyDataSetChanged();
            }
          }
        } else {
          item.setChecked(!item.isChecked());
          holder.checkBox.setChecked(item.isChecked());
          if(!item.isChecked()) {
              for(PickerItem it : items) {
                if(it.getId() == item.getParentId()) {
                  it.setChecked(false);
                  break;
                }
              }
          }
          listener.onItemClicked(item);
          notifyDataSetChanged();
        }
      }
    });
  }

  @Override public int getItemCount() {
    return items.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView countryFlagImageView;
    private TextView itemNameText;
    private LinearLayout rootView;
    private CheckBox checkBox;

    ViewHolder(View itemView) {
      super(itemView);
      checkBox = itemView.findViewById(R.id.country_checkbox);
      itemNameText = itemView.findViewById(R.id.country_title);
      rootView = itemView.findViewById(R.id.rootView);
    }
  }
}
