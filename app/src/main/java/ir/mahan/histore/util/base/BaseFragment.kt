package ir.mahan.histore.util.base


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.util.network.NetworkChecker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var networkChecker: NetworkChecker

    //Other
    var isNetworkAvailable = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Check network
        lifecycleScope.launch {
            networkChecker.checkForNetworkAvailability().collect {
                isNetworkAvailable = it
                if (!it) {
                    Toast.makeText(requireContext(), R.string.checkYourNetwork, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}