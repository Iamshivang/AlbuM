package com.example.album.ui.recent

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.album.R
import com.example.album.databinding.FragmentCollectionsBinding
import com.example.album.databinding.FragmentRecentBinding
import com.example.album.ui.fullScreenPhoto.FullscreenActivity
import com.example.album.ui.imageViewer.ImagerViewerActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecentFragment : Fragment() {

    private lateinit var binding: FragmentRecentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecentBinding.inflate(inflater)

        setViews()
        return binding.root
    }

    private fun setViews() {
        val frameLayouts = listOf(
            binding.flIndia to binding.tvIndia.text.toString(),
            binding.flBlack to binding.tvBlack.text.toString(),
            binding.flCyberpunk to binding.tvCyberpunk.text.toString(),
            binding.flGaming to binding.tvGaming.text.toString(),
            binding.flSpace to binding.tvSpace.text.toString(),
            binding.flMinimallistic to binding.tvMinimalist.text.toString(),
            binding.flNature to binding.tvNature.text.toString(),
            binding.flAnime to binding.tvAnime.text.toString()
        )

        frameLayouts.forEach { (frameLayout, textValue) ->
            frameLayout.setOnClickListener {
                val intent = Intent(requireContext(), FullscreenActivity::class.java)
                intent.putExtra("category", textValue)
                startActivity(intent)
            }
        }
    }

}