package com.example.words;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {

    private WordViewModel wordViewModel;
    private Button vAdd;
    private EditText vEnglish, vMeaning;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        vAdd = activity.findViewById(R.id.button_add);
        vEnglish = activity.findViewById(R.id.english);
        vMeaning = activity.findViewById(R.id.meaning);
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        vEnglish.requestFocus();
        imm.showSoftInput(vEnglish, 0);

        vAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = vEnglish.getEditableText().toString().trim();
                String meaning = vMeaning.getEditableText().toString().trim();
                if (english.isEmpty() || meaning.isEmpty()) {
                    Toast.makeText(activity, "请填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                Word word = new Word(english, meaning);
                wordViewModel.insertWords(word);
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
    }
}
