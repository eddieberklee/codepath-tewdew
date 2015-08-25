package compscieddy.com.tewdew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by elee in Aug 2015.
 */
public class EditTodoActivity extends Activity {

  public static final String SELECTED_TODO_POSITION_INDEX = "selected_position_index";
  public static final String SELECTED_TODO_TITLE = "selected_todo_title";
  public static final String UPDATED_TODO_POSITION_INDEX = "updated_todo_position_index";
  public static final String UPDATED_TODO_TITLE = "updated_todo_title";

  public static final int EDIT_TITLE_ACTIVITY_REQUEST = 1;

  int mPositionIndex;
  String mTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_todo);
    
    final EditText editTitle = (EditText) findViewById(R.id.edit_todo_title);
    TextView saveTitleButton = (TextView) findViewById(R.id.save_todo_title_button);
    
    mPositionIndex = getIntent().getIntExtra(SELECTED_TODO_POSITION_INDEX, -1);
    mTitle = getIntent().getStringExtra(SELECTED_TODO_TITLE);

    if (mTitle != null) {
      editTitle.setText(mTitle);
    }

    saveTitleButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(UPDATED_TODO_TITLE, editTitle.getText().toString());
        intent.putExtra(UPDATED_TODO_POSITION_INDEX, mPositionIndex);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }
}
