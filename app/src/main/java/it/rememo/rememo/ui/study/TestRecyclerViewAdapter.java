package it.rememo.rememo.ui.study;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowRadioCollectionBinding;
import it.rememo.rememo.databinding.RowSubmitItemBinding;
import it.rememo.rememo.databinding.RowTestItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;

public class TestRecyclerViewAdapter extends RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder> {

    private static final int VIEW_TYPE_TEST_ROW = 0;
    private static final int VIEW_TYPE_SUBMIT_BTN = 1;
    private boolean showResults = false;
    private TestActivity context;

    private final List<CollectionWord> list = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();
    protected LayoutInflater mInflater;

    public TestRecyclerViewAdapter(TestActivity context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SUBMIT_BTN) {
            RowSubmitItemBinding binding = RowSubmitItemBinding.inflate(mInflater, parent, false);
            return new ViewHolder(binding);
        } else {
            RowTestItemBinding binding = RowTestItemBinding.inflate(mInflater, parent, false);
            return new ViewHolder(binding);
        }

    }

    public void addAll(List<CollectionWord> c) {
        int sizeBefore = list.size();
        list.addAll(c);
        for (CollectionWord w : c)
            answers.add("");
        notifyItemRangeInserted(sizeBefore, c.size());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.binding != null) {
            CollectionWord w = list.get(position);
            if (showResults) {
                String an = answers.get(position);
                holder.binding.txtCollectionRow.setText(w.getOriginal() + " - " + w.getTranslated());
                holder.binding.editTextAnswer.setText(an);
                holder.binding.editTextAnswer.setEnabled(false);
                DrawableCompat.setTint(holder.binding.editTextAnswer.getBackground(), context.getColor(
                        an.trim().toLowerCase().equals(w.getTranslated().trim().toLowerCase()) ? R.color.rememo_dark : R.color.error_red
                ));

            } else {
                holder.binding.txtCollectionRow.setText(w.getOriginal());
            }
        } else if (holder.bindingSubmit != null) {
            if (showResults) {
                holder.bindingSubmit.btnSubmit.setText("Exit");
                holder.bindingSubmit.btnSubmit.setOnClickListener(v -> {
                    context.finish();
                });
            } else {
                holder.bindingSubmit.btnSubmit.setText("Submit");
                holder.bindingSubmit.btnSubmit.setOnClickListener(v -> {
                    if (!showResults) {
                        showResults = true;
                        notifyDataSetChanged();
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == list.size()) ? VIEW_TYPE_SUBMIT_BTN : VIEW_TYPE_TEST_ROW;
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowTestItemBinding binding = null;
        RowSubmitItemBinding bindingSubmit = null;

        public ViewHolder(RowTestItemBinding binding) {
            super(binding.getRoot());
            binding.editTextAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void afterTextChanged(Editable s) {
                    answers.set(getAdapterPosition(), s.toString());
                }
            });
            this.binding = binding;
        }

        public ViewHolder(RowSubmitItemBinding binding) {
            super(binding.getRoot());
            this.bindingSubmit = binding;
        }
    }

}