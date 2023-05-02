package io.github.jing.work.assistant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.map.geolocation.TencentLocationManager
import io.github.jing.work.assistant.databinding.ActivityMainBinding
import io.github.jing.work.assistant.gitlab.GitlabActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        TencentLocationManager.setUserAgreePrivacy(true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val functions = listOf(Function("自动打卡", R.drawable.baseline_work_24, "auto-clock-in"),
            Function("Gitlab", R.drawable.baseline_code_24, "gitlab/home"),
            Function("求助", R.drawable.baseline_help_24, "help")
        )
        val adapter = FunctionAdapter(this, functions)
        adapter.setOnClickFunctionListener(object: FunctionViewHolder.OnClickFunctionListener {
            override fun onClickFunction(data: Function) {
                if ("auto-clock-in" == data.route) {
                    startActivity(Intent(this@MainActivity, AutoClockInActivity::class.java))
                } else if ("gitlab/home" == data.route) {
                    startActivity(Intent(this@MainActivity, GitlabActivity::class.java))
                }
            }
        })
        recyclerView.adapter = adapter

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}