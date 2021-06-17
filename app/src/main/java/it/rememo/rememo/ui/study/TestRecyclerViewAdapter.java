package it.rememo.rememo.ui.study;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowSubmitItemBinding;
import it.rememo.rememo.databinding.RowTestItemBinding;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.Stat;
import it.rememo.rememo.utils.Common;


// test items to answer
public class TestRecyclerViewAdapter extends RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder> {

    private static final int VIEW_TYPE_TEST_ROW = 0;
    private static final int VIEW_TYPE_SUBMIT_BTN = 1;
    private boolean showResults = false;
    private final TestActivity context;

    private final List<CollectionWord> list = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();
    final protected LayoutInflater mInflater;

    public TestRecyclerViewAdapter(TestActivity context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // Reload previous answers
        if (viewType == VIEW_TYPE_SUBMIT_BTN) {
            RowSubmitItemBinding binding = RowSubmitItemBinding.inflate(mInflater, parent, false);
            return new ViewHolder(binding);
        } else {
            RowTestItemBinding binding = RowTestItemBinding.inflate(mInflater, parent, false);
            return new ViewHolder(binding);
        }

    }

    // Add all tests question
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
                        Common.checkAnswer(w.getTranslated(), an) ? R.color.rememo_dark : R.color.error_red
                ));

            } else {
                holder.binding.txtCollectionRow.setText(w.getOriginal());
                holder.binding.editTextAnswer.setText(answers.get(position));
            }
        } else if (holder.bindingSubmit != null) {
            if (showResults) {
                // Return to homw
                holder.bindingSubmit.btnSubmit.setText(R.string.exit);
                holder.bindingSubmit.btnSubmit.setOnClickListener(v -> context.finish());
            } else {
                holder.bindingSubmit.btnSubmit.setText(R.string.submit);
                holder.bindingSubmit.btnSubmit.setOnClickListener(v -> {
                    // test results
                    int tot = list.size(), right = 0;
                    for (int i = 0; i < list.size(); i++) {
                        CollectionWord w = list.get(i);
                        String an = answers.get(i);
                        boolean res = Common.checkAnswer(w.getTranslated(), an);
                        Stat.add(res, w.getCollectionParentId());
                        // rights words
                        if (res) right++;
                    }

                    // Show results
                    new AlertDialog.Builder(mInflater.getContext())
                            .setTitle(mInflater.getContext().getString(R.string.form_test_completed))
                            .setMessage(String.format(mInflater.getContext().getString(R.string.form_test_completed_results), right, tot))
                            .setPositiveButton(mInflater.getContext().getString(R.string.form_continue), (dialog, whichButton) -> { })
                            .show();

                    // results are in, reload to show correct and wrong
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
                    // Check if correct
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