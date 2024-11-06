/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */
package com.example.codeexam.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.codeexam.R

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 설정된 RecyclerView를 사용하여 뷰 페이징 구현
        val recyclerView = findViewById<WearableRecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            // WearableLinearLayoutManager를 사용하여 착용할 수 있는 장치에서 스크롤을 쉽게 합니다.
            layoutManager = WearableLinearLayoutManager(this@MainActivity)
            // 어댑터를 설정합니다.
            adapter = SensorAdapter()
            //isEdgeItemsCenteringEnabled = true // 중간에 요소가 위치하도록 설정
        }
    }
}
