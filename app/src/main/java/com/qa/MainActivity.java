package com.qa;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qa.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mainBinding;

    ArrayList<Question> questions;
    int questionCounter = 0;

    int correct = 0;
    int wrong = 0;

    String selectedAnswer ="";

    interface Types {
        String MULTIPLE = "multiple";
        String INPUT = "input";
        String YESNO = "yesorno";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mainBinding.toolbar);

        questions = (ArrayList<Question>) QuestionUtils.getQuestions(this);

        setListeners();
        mainBinding.startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestionnaire();
            }
        });
    }

    private void setListeners() {
        mainBinding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = questions.get(questionCounter);
                question.setSelectedAnswer(selectedAnswer);
                if(selectedAnswer.equals(question.getAnswer())) {
                    correct++;
                } else {
                    wrong++;
                }
                questionCounter++;
                if (questionCounter < questions.size()) {
                    startQuestionnaire();
                } else {
                    showStatusDialog();
                }
            }
        });
        mainBinding.multiQuestionRadioGroup.setOnCheckedChangeListener(multiCheckedListener);
        mainBinding.yesNoQuestionRadioGroup.setOnCheckedChangeListener(yesOrNoCheckedListener);
        mainBinding.inputEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mainBinding.next.setEnabled(true);
                    mainBinding.next.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.colorPrimaryDark));
                } else {
                    mainBinding.next.setEnabled(false);
                    mainBinding.next.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.text_color));
                }

                selectedAnswer = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    RadioGroup.OnCheckedChangeListener multiCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            mainBinding.next.setEnabled(true);
            mainBinding.next.setTextColor(ContextCompat.getColor(MainActivity.this,
                    R.color.colorPrimaryDark));

            if (mainBinding.radioButton1.getId() == i) {
                selectedAnswer = mainBinding.radioButton1.getText().toString();

            } else if (mainBinding.radioButton2.getId() == i) {
                selectedAnswer = mainBinding.radioButton2.getText().toString();

            } else if (mainBinding.radioButton3.getId() == i) {
                selectedAnswer = mainBinding.radioButton3.getText().toString();

            } else {
                selectedAnswer = mainBinding.radioButton4.getText().toString();
            }
        }
    };

    RadioGroup.OnCheckedChangeListener yesOrNoCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            mainBinding.next.setEnabled(true);
            mainBinding.next.setTextColor(ContextCompat.getColor(MainActivity.this,
                    R.color.colorPrimaryDark));
            if (mainBinding.radioButtonYes.getId() == i) {
                selectedAnswer = mainBinding.radioButtonYes.getText().toString();

            } else if (mainBinding.radioButtonNo.getId() == i) {
                selectedAnswer = mainBinding.radioButtonNo.getText().toString();

            }
        }
    };

    public void showStatusDialog() {

        mainBinding.questionProgress.setProgress(mainBinding.questionProgress.getMax());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.status_dialog, null);
        builder.setView(view);


        TextView restart = (TextView) view.findViewById(R.id.restart);
        TextView correct = (TextView) view.findViewById(R.id.correct);
        TextView wrong = (TextView) view.findViewById(R.id.wrong);

        correct.setText(getString(R.string.correct, String.valueOf(MainActivity.this.correct)));
        wrong.setText(getString(R.string.wrong, String.valueOf(MainActivity.this.wrong)));

        builder.setCancelable(false);
        final AlertDialog dialog = builder.show();

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                questionCounter = 0;
                MainActivity.this.correct = 0;
                MainActivity.this.wrong = 0;
                selectedAnswer = "";
                startQuestionnaire();
            }
        });
    }

    private void startQuestionnaire() {
        if (questions != null) {

            mainBinding.inputEdiText.setText("");
            mainBinding.multiQuestionRadioGroup.clearCheck();
            mainBinding.yesNoQuestionRadioGroup.clearCheck();
            mainBinding.next.setEnabled(false);
            mainBinding.next.setTextColor(ContextCompat.getColor(MainActivity.this,
                    R.color.text_color));

            Question question = questions.get(questionCounter);
            mainBinding.questionProgress.setMax(questions.size());
            mainBinding.questionProgress.setProgress(questionCounter);
            mainBinding.question.setText(question.getQuestion());
            if (question.getType().equals(Types.MULTIPLE)) {
                mainBinding.inputEdiText.setVisibility(View.GONE);
                mainBinding.yesNoQuestionRadioGroup.setVisibility(View.GONE);
                mainBinding.multiQuestionRadioGroup.setVisibility(View.VISIBLE);
                mainBinding.multiQuestionRadioGroup.setTag(questionCounter);
                if (question.getOptions() != null) {
                    mainBinding.radioButton1.setText(question.getOptions().get(0));
                    mainBinding.radioButton2.setText(question.getOptions().get(1));
                    mainBinding.radioButton3.setText(question.getOptions().get(2));
                    mainBinding.radioButton4.setText(question.getOptions().get(3));
                }
            } else if (question.getType().equals(Types.YESNO)) {
                mainBinding.yesNoQuestionRadioGroup.setTag(questionCounter);
                mainBinding.inputEdiText.setVisibility(View.GONE);
                mainBinding.yesNoQuestionRadioGroup.setVisibility(View.VISIBLE);
                mainBinding.multiQuestionRadioGroup.setVisibility(View.GONE);
            } else {
                mainBinding.inputEdiText.setVisibility(View.VISIBLE);
                mainBinding.yesNoQuestionRadioGroup.setVisibility(View.GONE);
                mainBinding.multiQuestionRadioGroup.setVisibility(View.GONE);
            }
            if (mainBinding.welcomeLayout.getVisibility() == View.VISIBLE) {
                mainBinding.welcomeLayout.setVisibility(View.GONE);
                mainBinding.questionLayout.setVisibility(View.VISIBLE);
            }
        }


    }

}
