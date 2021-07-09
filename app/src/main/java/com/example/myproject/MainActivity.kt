package com.example.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.databinding.ActivityMainBinding
import com.example.myproject.databinding.ItemTodoBinding
import retrofit2.HttpException
import retrofit2.http.Query
import java.io.IOException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var todoBinding: ItemTodoBinding

    private var custom: ArrayList<Todo> = arrayListOf()

    private var matchedCustom: ArrayList<Todo> = arrayListOf()

    private var myAdapter: MyAdapter = MyAdapter(custom)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            lifecycleScope.launchWhenCreated {
                val response = try {
                    RetrofitInstance.api.getTodos()
                } catch (e: IOException) {
                    Log.e(TAG, "IOException, You might not have internet connection")
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response")
                    return@launchWhenCreated
                }
                val any = if (response.isSuccessful && response.body() != null) {
                    myAdapter.todos = (response.body() as ArrayList<Todo>)!!
                    custom = (response.body() as ArrayList<Todo>)!!
                } else {
                    Log.e(TAG, "Response Not Successful")
                }
            }
        setupRecyclerView()
        performSearch()
    }

    private fun setupRecyclerView() = binding.viewTodo.apply{
        myAdapter = MyAdapter(custom).also {
            binding.viewTodo.adapter = it
            binding.viewTodo.adapter!!.notifyDataSetChanged()
        }
        binding.searchBar.isSubmitButtonEnabled = true
        layoutManager = LinearLayoutManager(this@MainActivity)
    }


    private fun performSearch() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }
    private fun search(text: String?) {
        matchedCustom = arrayListOf()

        text?.let {
            custom.forEach { custom ->
                if (custom.title.contains(text, true) || custom.id.toString().contains(text, true))
                {
                    matchedCustom.add(custom)
                }
            }
            updateRecyclerView()
            if (matchedCustom.isEmpty()) {
                Toast.makeText(this, "No Matches Found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }
    private fun updateRecyclerView() {
        binding.viewTodo.apply {
            myAdapter.todos = matchedCustom
            myAdapter.notifyDataSetChanged()
        }
    }

}