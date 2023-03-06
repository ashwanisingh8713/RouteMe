package com.route.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonElement
import com.route.modal.RoutesData
import com.route.routeme.R
import com.route.routeme.RouteArPath_v2
import com.route.routeme.databinding.FragmentFirstBinding
import com.route.viewmodel.RoutesDataViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Ashwani Kumar Singh on 09,February,2023.
 */
class FirstFragment:Fragment() {

    private val mDisposable = CompositeDisposable()

    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::listenBarCodeResult)
    private lateinit var binding: FragmentFirstBinding
    private lateinit var model: RoutesDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openRouteScreen() {
        val intent = Intent(requireActivity(), RouteArPath_v2::class.java)
        intent.putExtra("id", "202204190137151835938906")
        startActivity(intent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity())[RoutesDataViewModel::class.java]
        model.clearDisposable()

        binding.barcodeBtn.setOnClickListener{
            scanQrCode.launch(null)
        }

        binding.databaseBtn.setOnClickListener {
            model.loadDatabaseList()
        }

        binding.collectionBtn.setOnClickListener { model.loadRoutesMeDataCollection() }

        binding.documentBtn.setOnClickListener {
            //model.loadRoutesDocument();
            model.loadRoutesDocumentJson()
        }

        model.routesDocument.observe(requireActivity()) { routesData: RoutesData ->
            // update UI
            val routes = routesData.documents
            binding.resText.text = routes[0].id
        }

        model.routesDocumentJson.observe(
            requireActivity()
        ) { routesJsonData: JsonElement ->
            // update UI
            if (binding != null && binding.resText != null) {
                binding.resText.text = routesJsonData.toString()
            }
        }

        model.collection.observe(
            requireActivity()
        ) { collectionJson: JsonElement ->
            // update UI
            binding.resText.text = collectionJson.toString()
        }

        model.databaseList.observe(
            requireActivity()
        ) { databaseJson: JsonElement ->
            // update UI
            binding.resText.text = databaseJson.toString()
        }

        model.routesError.observe(
            requireActivity()
        ) { error: String? ->
            binding.resText.text = error
        }

    }

    override fun onResume() {
        super.onResume()
        binding.title.text = "Scan The Bar Code"
    }

    override fun onDestroyView() {
        mDisposable.clear()
        mDisposable.dispose()
        super.onDestroyView()
    }

    private fun listenBarCodeResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                var requiredValue = result.content.rawValue
                val navController = NavHostFragment.findNavController(this@FirstFragment)
                val bundle = Bundle()
                //bundle.putString("Url", requiredValue)
                navController.setGraph(R.navigation.nav_graph, bundle)
                navController.navigate(R.id.action_FirstFragment_to_DocumentFragment)
                binding.title.text = "List of Routes"

            }
            QRResult.QRUserCanceled -> {
                showSnackBar("User canceled")
            }
            QRResult.QRMissingPermission -> {
                showSnackBar("Missing permission")
            }

            is QRResult.QRError -> {
                val msg = "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
                showSnackBar(msg)
            }
        }

    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
                maxLines = 5
                setTextIsSelectable(true)
            }
        }.show()
    }

}