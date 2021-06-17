package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowWordItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

// recyclerView that holds the words
public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder> {

    private final  List<CollectionWord> words;
    private final  LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    WordsRecyclerViewAdapter(Context context, List<CollectionWord> words) {
        this.mInflater = LayoutInflater.from(context);
        //List of words
        this.words = words;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RowWordItemBinding binding = RowWordItemBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectionWord w = words.get(position);
        holder.setCollection(w);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    // Remove from list
    public void removeAt(int i) {
        words.remove(i);
        notifyItemRemoved(i);
    }

    // Multiple add to list
    public void resetAll(List<CollectionWord> list) {
        words.clear();
        words.addAll(list);
        notifyDataSetChanged();
    }

    // Add to list
    public void add(CollectionWord w) {
        words.add(w);
        notifyItemInserted(words.size() - 1);
    }

    // set on word click event
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final  RowWordItemBinding binding;
        private CollectionWord word = null;
        private final  WordsRecyclerViewAdapter adapterReference;

        ViewHolder(RowWordItemBinding binding, WordsRecyclerViewAdapter adapterReference) {
            super(binding.getRoot());
            this.adapterReference = adapterReference;

            this.binding = binding;

            binding.cardView.setOnClickListener(view -> {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            });

            binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                // menu.setHeaderTitle("Select The Action");
                menu.add(0, view.getId(), 0, view.getContext().getString(R.string.basic_edit)).setOnMenuItemClickListener((mItem) -> renameWord());
                menu.add(0, view.getId(), 0, view.getContext().getString(R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteWord());
            });
        }

        // Rename a word translation and original
        private boolean renameWord() {
            final EditText txtOriginal = new EditText(itemView.getContext());
            final EditText txtTranslated = new EditText(itemView.getContext());
            txtOriginal.setInputType(InputType.TYPE_CLASS_TEXT);
            txtTranslated.setInputType(InputType.TYPE_CLASS_TEXT);
            txtOriginal.setHint(itemView.getContext().getString(R.string.word_original));
            txtTranslated.setHint(itemView.getContext().getString(R.string.word_translated));
            if (word != null) {
                txtOriginal.setText(word.getOriginal());
                txtTranslated.setText(word.getTranslated());
            }

            // open rename dialog
            Alerts
                .getInputTextAlert(itemView.getContext(), txtOriginal, txtTranslated)
                .setTitle(itemView.getContext().getString(R.string.word_edit))
                .setPositiveButton(itemView.getContext().getString(R.string.basic_rename), (dialog, which) -> {
                    String original = txtOriginal.getText().toString();
                    String translated = txtTranslated.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(CollectionWord.KEY_ORIGINAL, original);
                    updateColl.put(CollectionWord.KEY_TRANSLATED, translated);
                    word.updateFirestore(updateColl,
                        x -> {
                            word.setOriginal(original);
                            word.setTranslated(translated);
                            updateUI();
                        },
                        ex -> Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.coll_cant_rename))
                    );
                })
                .setNegativeButton(itemView.getContext().getString(R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                .show();
            return true;
        }


        private boolean deleteWord() {
            Collection coll = word.getCollectionParent();
            if (coll == null) {
                return false;
            }

            // Ask confirmation
            new AlertDialog.Builder(itemView.getContext())
                .setTitle(itemView.getContext().getString(R.string.word_delete))
                .setMessage(String.format(itemView.getContext().getString(R.string.form_sure_to_delete_STR), word.getOriginal()))
                .setPositiveButton(itemView.getContext().getString(R.string.form_im_sure), (dialog, whichButton) ->
                    coll.deleteWord(word,
                            x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.word_deleted)); },
                            ex -> Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.word_cant_delete))
                    )
                )
                .setNegativeButton(itemView.getContext().getString(R.string.basic_cancel), null).show();
            return true;
        }

        public void setCollection(CollectionWord w) {
            word = w;
            updateUI();
        }

        private void updateUI() {
            binding.txtOriginalWordItem.setText(word.getOriginal());
            binding.txtTranslatedWordItem.setText(word.getTranslated());
        }
    }

}

