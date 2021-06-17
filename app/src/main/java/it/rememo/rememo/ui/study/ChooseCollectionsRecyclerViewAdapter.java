package it.rememo.rememo.ui.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.databinding.RowRadioCollectionBinding;
import it.rememo.rememo.models.Collection;

public class ChooseCollectionsRecyclerViewAdapter extends RecyclerView.Adapter<ChooseCollectionsRecyclerViewAdapter.ViewHolder> {

    private final List<Collection> list = new ArrayList<>();
    final protected LayoutInflater mInflater;
    private int lastCheckedPositionRadio = -1;
    private final List<Boolean> checkedCheckBoxes = new ArrayList<>();
    private boolean multiselect = false;

    public ChooseCollectionsRecyclerViewAdapter(Context context, boolean multiselect) {
        this.mInflater = LayoutInflater.from(context);
        this.multiselect = multiselect;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RowRadioCollectionBinding binding = RowRadioCollectionBinding.inflate(mInflater, parent, false);
        if (multiselect)
            binding.radioButton.setVisibility(View.GONE);
        else
            binding.checkBox.setVisibility(View.GONE);
        return new ViewHolder(binding);
    }

    public void addAll(List<Collection> c) {
        int sizeBefore = list.size();
        for (int i = 0; i < c.size(); i++) {
            checkedCheckBoxes.add(false);
        }
        list.addAll(c);
        notifyItemRangeInserted(sizeBefore, c.size());
    }
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        String t = list.get(position).getName();
        if (multiselect) {
            holder.binding.checkBox.setText(t);
            holder.binding.checkBox.setChecked(checkedCheckBoxes.get(position));
        } else {
            holder.binding.radioButton.setText(t);
            holder.binding.radioButton.setChecked(position == lastCheckedPositionRadio);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<String> getSelectedIds() {
        ArrayList<String> res = new ArrayList<>();

        if (multiselect) {
            for (int i = 0; i < list.size(); i++) {
                if (checkedCheckBoxes.get(i)) {
                    res.add(list.get(i).getId());
                }
            }
        } else {
            if (lastCheckedPositionRadio != -1) {
                res.add(list.get(lastCheckedPositionRadio).getId());
            }
        }

        return res;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowRadioCollectionBinding binding;

        public ViewHolder(RowRadioCollectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.radioButton.setOnClickListener(v -> {
                int copyOfLastCheckedPosition = lastCheckedPositionRadio;
                lastCheckedPositionRadio = getAdapterPosition();
                notifyItemChanged(copyOfLastCheckedPosition);
                notifyItemChanged(lastCheckedPositionRadio);
            });
            this.binding.checkBox.setOnClickListener(v ->
                checkedCheckBoxes.set(
                        getAdapterPosition(),
                        this.binding.checkBox.isChecked()
                )
            );
        }
    }

}