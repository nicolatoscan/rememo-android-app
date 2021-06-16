package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowWordItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder> {

    private List<CollectionWord> words;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    WordsRecyclerViewAdapter(Context context, List<CollectionWord> words) {
        this.mInflater = LayoutInflater.from(context);
        this.words = words;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public void removeAt(int i) {
        words.remove(i);
        notifyItemRemoved(i);
    }

    public void resetAll(List<CollectionWord> list) {
        words.clear();
        words.addAll(list);
        notifyDataSetChanged();
    }

    public void add(CollectionWord w) {
        words.add(w);
        notifyItemInserted(words.size() - 1);
    }

    CollectionWord getItem(int id) {
        return words.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowWordItemBinding binding;
        private CollectionWord word = null;
        private WordsRecyclerViewAdapter adapterReference;

        ViewHolder(RowWordItemBinding binding, WordsRecyclerViewAdapter adapterReference) {
            super(binding.getRoot());
            this.adapterReference = adapterReference;

            this.binding = binding;

            binding.cardView.setOnClickListener(view -> {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            });

            this.binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                // menu.setHeaderTitle("Select The Action");
                menu.add(0, view.getId(), 0, Common.resStr(view.getContext(), R.string.basic_edit)).setOnMenuItemClickListener((mItem) -> renameWord());
                menu.add(0, view.getId(), 0, Common.resStr(view.getContext(), R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteWord());
            });
        }

        private boolean renameWord() {
            final EditText txtOriginal = new EditText(itemView.getContext());
            final EditText txtTranslated = new EditText(itemView.getContext());
            txtOriginal.setInputType(InputType.TYPE_CLASS_TEXT);
            txtTranslated.setInputType(InputType.TYPE_CLASS_TEXT);
            txtOriginal.setHint(Common.resStr(itemView.getContext(), R.string.word_original));
            txtTranslated.setHint(Common.resStr(itemView.getContext(), R.string.word_translated));
            if (word != null) {
                txtOriginal.setText(word.getOriginal());
                txtTranslated.setText(word.getTranslated());
            }

            Alerts
                    .getInputTextAlert(itemView.getContext(), txtOriginal, txtTranslated)
                    .setTitle(Common.resStr(itemView.getContext(), R.string.word_edit))
                    .setPositiveButton(Common.resStr(itemView.getContext(), R.string.basic_rename), (dialog, which) -> {
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
                            ex -> Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.coll_cant_rename))
                        );
                    })
                    .setNegativeButton(Common.resStr(itemView.getContext(), R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                    .show();
            return true;
        }

        private boolean deleteWord() {
            Collection coll = word.getCollectionParent();
            if (coll == null) {
                return false;
            }
            new AlertDialog.Builder(itemView.getContext())
                .setTitle(Common.resStr(itemView.getContext(), R.string.word_delete))
                .setMessage(String.format(Common.resStr(itemView.getContext(), R.string.form_sure_to_delete_STR), word.getOriginal()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(Common.resStr(itemView.getContext(), R.string.form_im_sure), (dialog, whichButton) -> {
                    coll.deleteWord(word,
                            x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.word_deleted)); },
                            ex -> Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.word_cant_delete))
                    );
                })
                .setNegativeButton(Common.resStr(itemView.getContext(), R.string.basic_cancel), null).show();
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

