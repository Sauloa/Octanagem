package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aaaemec.octanagem.R

import kotlinx.android.synthetic.main.home_fragment.*

class ParceiroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater
            .inflate(
                R.layout.parceiros_fragment,
                container,
                false
            )
    }

}
