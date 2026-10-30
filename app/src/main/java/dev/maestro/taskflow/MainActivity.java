package dev.maestro.taskflow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Insets;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Offline task manager. Native resource IDs form the public UI automation contract. */
public final class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(24, 44, 67);
    private static final int TEAL = Color.rgb(8, 127, 140);
    private static final int MUTED = Color.rgb(88, 108, 126);
    private static final int BACKGROUND = Color.rgb(244, 247, 250);
    private static final int BORDER = Color.rgb(218, 228, 234);
    private static final String DATA_KEY = "tasks";
    private final List<Task> tasks = new ArrayList<>();
    private SharedPreferences preferences;
    private LinearLayout root;
    private boolean showingForm;

    private static final class Task {
        final String title;
        boolean completed;
        Task(String title, boolean completed) {
            this.title = title;
            this.completed = completed;
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 30) {
            // Handle all insets in one place; Android 15 requires edge-to-edge anyway.
            getWindow().setDecorFitsSystemWindows(false);
        }
        preferences = getSharedPreferences("taskflow", MODE_PRIVATE);
        loadTasks();
        if (savedInstanceState != null && savedInstanceState.getBoolean("showing_form")) {
            showForm();
            EditText input = findViewById(R.id.task_name_input);
            input.setText(savedInstanceState.getString("draft_title", ""));
            input.setSelection(input.length());
            if (savedInstanceState.getBoolean("validation_visible")) {
                TextView error = findViewById(R.id.validation_error);
                error.setText(savedInstanceState.getString("validation_text"));
                error.setVisibility(View.VISIBLE);
            }
        } else {
            showHome();
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showing_form", showingForm);
        if (showingForm) {
            EditText input = findViewById(R.id.task_name_input);
            TextView error = findViewById(R.id.validation_error);
            outState.putString("draft_title", input.getText().toString());
            outState.putBoolean("validation_visible", error.getVisibility() == View.VISIBLE);
            outState.putString("validation_text", error.getText().toString());
        }
    }

    private void loadTasks() {
        tasks.clear();
        try {
            JSONArray data = new JSONArray(preferences.getString(DATA_KEY, "[]"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                tasks.add(new Task(item.getString("title"), item.optBoolean("completed", false)));
            }
        } catch (JSONException ignored) {
            // A malformed local record is treated as an empty list; no remote dependency exists.
        }
    }

    private boolean persistTasks() {
        JSONArray data = new JSONArray();
        for (Task task : tasks) {
            JSONObject item = new JSONObject();
            try {
                item.put("title", task.title);
                item.put("completed", task.completed);
                data.put(item);
            } catch (JSONException impossible) {
                throw new IllegalStateException(impossible);
            }
        }
        // Synchronous commit ensures a force-stop immediately after save cannot race disk writing.
        return preferences.edit().putString(DATA_KEY, data.toString()).commit();
    }

    private void createRoot(int screenId) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setId(screenId);
        root.setBackgroundColor(BACKGROUND);
        root.setPadding(dp(24), dp(20), dp(24), dp(20));
        setContentView(root);
        // API 26-29 retain the platform's normal decor fitting and adjustResize behavior.
        // Adding system-bar insets again on those versions would double their spacing.
        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                Insets bars = windowInsets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(dp(24) + bars.left, dp(20) + bars.top,
                        dp(24) + bars.right, dp(20) + bars.bottom);
                return windowInsets;
            });
            root.requestApplyInsets();
        }
    }

    private void showHome() {
        showingForm = false;
        createRoot(R.id.home_screen);
        TextView brand = text("TASKFLOW", 13, TEAL, true);
        brand.setLetterSpacing(0.16f);
        root.addView(brand);
        TextView title = text("Tus tareas,\na tu ritmo.", 32, NAVY, true);
        root.addView(title, spacing(-1, -2, 10, 10));
        root.addView(text("Un espacio simple para organizar tu día.", 15, MUTED, false));

        TextView count = text(summary(), 15, TEAL, true);
        count.setId(R.id.task_count);
        count.setPadding(dp(16), dp(16), dp(16), dp(16));
        count.setBackground(shape(Color.rgb(225, 242, 243), 16, 0));
        root.addView(count, spacing(-1, -2, 24, 16));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout list = new LinearLayout(this);
        list.setId(R.id.task_list);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);
        if (tasks.isEmpty()) {
            LinearLayout empty = new LinearLayout(this);
            empty.setGravity(Gravity.CENTER);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setPadding(dp(20), dp(42), dp(20), dp(42));
            empty.setBackground(shape(Color.WHITE, 20, BORDER));
            TextView mark = text("✓", 44, TEAL, true);
            empty.addView(mark);
            TextView message = text("Todavía no hay tareas", 19, NAVY, true);
            message.setId(R.id.empty_state);
            message.setGravity(Gravity.CENTER);
            empty.addView(message, spacing(-1, -2, 14, 8));
            TextView hint = text("Crea tu primera tarea y da el siguiente paso.", 14, MUTED, false);
            hint.setGravity(Gravity.CENTER);
            empty.addView(hint);
            list.addView(empty);
        } else {
            for (Task task : tasks) addTaskRow(list, task, count);
        }
        Button create = button("+  Nueva tarea", R.id.new_task_button);
        create.setOnClickListener(v -> showForm());
        root.addView(create, spacing(-1, dp(56), 20, 0));
    }

    private void addTaskRow(LinearLayout list, Task task, TextView count) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(16), dp(16), dp(16));
        card.setBackground(shape(Color.WHITE, 16, BORDER));
        CheckBox check = new CheckBox(this);
        check.setId(R.id.task_complete);
        check.setContentDescription("Completar tarea: " + task.title);
        check.setChecked(task.completed);
        card.addView(check, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        TextView name = text(task.title, 17, NAVY, true);
        name.setId(R.id.task_title);
        TextView status = text(task.completed ? "Completada" : "Pendiente", 13, MUTED, false);
        status.setId(R.id.task_status);
        applyCompletedStyle(name, task.completed);
        labels.addView(name);
        labels.addView(status, spacing(-1, -2, 5, 0));
        card.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean previous = task.completed;
            task.completed = isChecked;
            if (!persistTasks()) {
                task.completed = previous;
                check.setChecked(previous);
                return;
            }
            applyCompletedStyle(name, task.completed);
            status.setText(task.completed ? "Completada" : "Pendiente");
            count.setText(summary());
        });
        list.addView(card, spacing(-1, -2, 0, 12));
    }

    private void applyCompletedStyle(TextView name, boolean completed) {
        name.setPaintFlags(completed ? name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        name.setTextColor(completed ? MUTED : NAVY);
    }

    private String summary() {
        int done = 0;
        for (Task task : tasks) if (task.completed) done++;
        return tasks.size() + (tasks.size() == 1 ? " tarea" : " tareas")
                + "  ·  " + done + (done == 1 ? " completada" : " completadas");
    }

    private void showForm() {
        showingForm = true;
        createRoot(R.id.form_screen);
        Button back = new Button(this);
        back.setText("‹  Volver a mis tareas");
        back.setId(R.id.back_button);
        back.setAllCaps(false);
        back.setTextColor(TEAL);
        back.setTextSize(14);
        back.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        back.setPadding(0, 0, 0, 0);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(v -> { hideKeyboard(); showHome(); });
        root.addView(back, new LinearLayout.LayoutParams(-1, dp(48)));
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(body);
        body.addView(text("Una nueva meta", 30, NAVY, true), spacing(-1, -2, 26, 10));
        body.addView(text("Anota algo que quieras hacer hoy.", 16, MUTED, false));
        TextView label = text("Nombre de la tarea *", 15, NAVY, true);
        body.addView(label, spacing(-1, -2, 32, 10));
        EditText input = new EditText(this);
        input.setId(R.id.task_name_input);
        label.setLabelFor(R.id.task_name_input);
        input.setHint("Ej.: Preparar la exposición");
        input.setTextColor(NAVY);
        input.setTextSize(16);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
        input.setPadding(dp(16), dp(15), dp(16), dp(15));
        input.setBackground(shape(Color.WHITE, 12, BORDER));
        body.addView(input, new LinearLayout.LayoutParams(-1, dp(58)));
        TextView error = text("El nombre de la tarea es obligatorio.", 14, Color.rgb(173, 41, 50), false);
        error.setId(R.id.validation_error);
        error.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        error.setVisibility(View.GONE);
        body.addView(error, spacing(-1, -2, 12, 0));
        body.addView(text("Hasta 120 caracteres. Tus tareas se guardan en este dispositivo.", 13, MUTED, false),
                spacing(-1, -2, 14, 0));
        Button save = button("Guardar tarea", R.id.save_task_button);
        save.setOnClickListener(v -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) {
                error.setText("El nombre de la tarea es obligatorio.");
                error.setVisibility(View.VISIBLE);
                hideKeyboard();
                return;
            }
            tasks.add(new Task(title, false));
            if (!persistTasks()) {
                tasks.remove(tasks.size() - 1);
                error.setText("No se pudo guardar. Inténtalo de nuevo.");
                error.setVisibility(View.VISIBLE);
                return;
            }
            hideKeyboard();
            showHome();
        });
        root.addView(save, spacing(-1, dp(56), 20, 0));
    }

    @Override public void onBackPressed() {
        if (showingForm) { hideKeyboard(); showHome(); }
        else super.onBackPressed();
    }

    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    private Button button(String title, int id) {
        Button button = new Button(this);
        button.setId(id);
        button.setText(title);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackground(shape(TEAL, 14, 0));
        button.setStateListAnimator(null);
        return button;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setFontFeatureSettings("kern");
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable shape(int fill, int radius, int stroke) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fill);
        background.setCornerRadius(dp(radius));
        if (stroke != 0) background.setStroke(dp(1), stroke);
        return background;
    }

    private LinearLayout.LayoutParams spacing(int width, int height, int top, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(0, dp(top), 0, dp(bottom));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
