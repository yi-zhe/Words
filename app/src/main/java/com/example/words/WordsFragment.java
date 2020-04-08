package com.example.words;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {

    private WordViewModel wordViewModel;
    private RecyclerView vWords;
    private MyAdapter myAdapter1, myAdapter2;
    private FloatingActionButton vAdd;
    private LiveData<List<Word>> filteredWords;
    private static final String VIEW_TYPE = "view_type";
    private static final String IS_USING_CARD = "using_card";

    public WordsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        vWords = requireActivity().findViewById(R.id.words);
        vWords.setLayoutManager(new LinearLayoutManager(requireActivity()));
        vWords.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager manager = (LinearLayoutManager) vWords.getLayoutManager();
                int first = manager.findFirstVisibleItemPosition();
                int last = manager.findLastVisibleItemPosition();
                for (int i = first; i <= last; i++) {
                    MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) vWords.findViewHolderForAdapterPosition(i);
                    holder.textViewNumber.setText(String.valueOf(i + 1));
                }
            }
        });
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD, false);
        if (viewType) {
            vWords.setAdapter(myAdapter2);
        } else {
            vWords.setAdapter(myAdapter1);
        }
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                if (temp != words.size()) {
                    vWords.smoothScrollBy(0, -200);
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }
            }
        });
        vAdd = requireActivity().findViewById(R.id.floatingActionButton);
        vAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(700);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredWords = wordViewModel.findWordsWithPattern(newText.trim());
                filteredWords.removeObservers(requireActivity());
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();
                        if (temp != words.size()) {
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.deleteAllWords();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
                break;

            case R.id.switchView:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD, false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (viewType) {
                    vWords.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD, false);
                } else {
                    vWords.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD, true);
                }
                editor.apply();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
