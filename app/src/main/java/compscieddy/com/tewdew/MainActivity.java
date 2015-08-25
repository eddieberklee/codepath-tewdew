package compscieddy.com.tewdew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

  private TextView mAddTodoButton;
  private EditText mAddTodoEditTitle;
  private ListView mTodoListView;
  private ArrayList<String> mTodoItemsList;
  private TodoListArrayAdapter mTodoItemsAdapter;

  private static final String appFileName = "compscieddy_todo.txt";
  private FrameLayout mAddTodoContainer;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    readItems();

    mAddTodoButton = (TextView) findViewById(R.id.add_todo_button);
    mAddTodoEditTitle = (EditText) findViewById(R.id.add_todo_title);
    mTodoListView = (ListView) findViewById(R.id.todo_list_view);
    mAddTodoContainer = (FrameLayout) findViewById(R.id.add_todo_container);

    mTodoItemsAdapter = new TodoListArrayAdapter(this, R.layout.todo_list_layout, mTodoItemsList);
    mTodoListView.setAdapter(mTodoItemsAdapter);
    mTodoListView.setDivider(null);

    /** Was trying to do a custom TouchListener thing but ran into issues :(
    mTodoListView.setOnTouchListener(new View.OnTouchListener() {
      boolean longClickActive = false;
      long clickStartTime;
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            Log.d("", "action_down");
            longClickActive = true;
            clickStartTime = System.currentTimeMillis();
            break;
          case MotionEvent.ACTION_UP:
            Log.d("", "action_up");
            longClickActive = false;
            long clickDuration = System.currentTimeMillis() - clickStartTime;
            Log.d("", "duration: " + clickDuration);
            break;
        }
        return true;
      }
    });
      */

    mTodoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
        mTodoItemsList.remove(pos);
        mTodoItemsAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
        writeItems();
        return true;
      }
    });

    mAddTodoEditTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
            && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
          addNewTodo();
          return true;
        }
        return false;
      }
    });

    mAddTodoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addNewTodo();
      }
    });
  }

  private void addNewTodo() {
    String todoTitle = mAddTodoEditTitle.getText().toString();
    if (!TextUtils.isEmpty(todoTitle)) {
      mAddTodoContainer.setBackgroundColor(getResources().getColor(R.color.add_todo_container_background));
      mTodoItemsAdapter.add(todoTitle);
//      hideKeyboard(); - actually hiding the keyboard feels weird
      mAddTodoEditTitle.setText("");
      mTodoListView.smoothScrollToPosition(mTodoItemsAdapter.getCount() - 1);
    } else { // empty text 0 characters error case
      mAddTodoContainer.setBackgroundColor(getResources().getColor(R.color.flatui_red1));
      Toast.makeText(MainActivity.this, "Tewdew can't be 0 characters", Toast.LENGTH_SHORT).show();
    }
    writeItems();
  }

  private void readItems() {
    File filesDir = getFilesDir();
    File todoFile = new File(filesDir, MainActivity.appFileName);
    try {
      mTodoItemsList = new ArrayList<String>(FileUtils.readLines(todoFile));
    } catch (IOException e) {
      mTodoItemsList = new ArrayList<>();
    }
  }

  private void writeItems() {
    File filesDir = getFilesDir();
    File todoFile = new File(filesDir, MainActivity.appFileName);
    try {
      FileUtils.writeLines(todoFile, mTodoItemsList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
  }

  private class TodoListArrayAdapter extends ArrayAdapter {
    private LayoutInflater inflater = null;
    private List list;

    private class ViewHolder {
      TextView title;
      ImageView editButton;
    }

    public TodoListArrayAdapter(Context context, int resource, List objects) {
      super(context, resource, objects);
      inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        convertView = inflater.inflate(R.layout.todo_list_layout, null);
        holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.list_title);
        holder.editButton = (ImageView) convertView.findViewById(R.id.edit_todo_button);
        final int finalPosition = position;
        holder.editButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EditTodoActivity.class);
            intent.putExtra(EditTodoActivity.SELECTED_TODO_POSITION_INDEX, finalPosition);
            intent.putExtra(EditTodoActivity.SELECTED_TODO_TITLE, mTodoItemsList.get(finalPosition));
            startActivityForResult(intent, EditTodoActivity.EDIT_TITLE_ACTIVITY_REQUEST);
          }
        });
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.title.setText((String) list.get(position));
      return convertView;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == EditTodoActivity.EDIT_TITLE_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      String updatedTitle = bundle.getString(EditTodoActivity.UPDATED_TODO_TITLE);
      int position = data.getIntExtra(EditTodoActivity.UPDATED_TODO_POSITION_INDEX, -1);
      if (position != -1 && !TextUtils.isEmpty(updatedTitle)) {
        Toast.makeText(MainActivity.this, "" + mTodoItemsList.get(position) + " --> " + updatedTitle, Toast.LENGTH_SHORT).show();
        mTodoItemsList.set(position, updatedTitle);
        mTodoItemsAdapter.notifyDataSetChanged();
        writeItems();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
