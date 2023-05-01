package io.github.jing.work.assistant

import android.Manifest
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import io.github.jing.work.assistant.data.Address
import io.github.jing.work.assistant.databinding.ActivityAutoClockInBinding
import io.github.jing.work.assistant.vm.AutoClockInViewModel
import io.github.jing.work.assistant.widget.AddressAdapter

class AutoClockInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAutoClockInBinding

    private lateinit var requestLocationLauncher: ActivityResultLauncher<Array<String>>
    private var timePickerDialog: TimePickerDialog? = null

    private lateinit var viewModel: AutoClockInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoClockInBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AutoClockInViewModel::class.java]
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        requestLocationLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)
                    && it[Manifest.permission.ACCESS_FINE_LOCATION] == false
                ) {
                    return@registerForActivityResult
                }
                if (it.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && it[Manifest.permission.ACCESS_COARSE_LOCATION] == false
                ) {
                    return@registerForActivityResult
                }
                viewModel.getWorkAddressOptions()
            }

        binding.tvAddress.isSelected = true
        binding.tvAddress.setOnClickListener {
            binding.tvAddress.isSelected = true
            binding.tvStartWorkTime.isSelected = false
            binding.tvOffWorkTime.isSelected = false
            //获取地址信息
            val list = mutableListOf<String>()
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(
                    this@AutoClockInActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                list.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(
                    this@AutoClockInActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (list.isEmpty()) {
                viewModel.getWorkAddressOptions()
                return@setOnClickListener
            }
            showLocationPermissionRationale(list.toTypedArray())
        }
        binding.tvStartWorkTime.setOnClickListener {
            binding.tvAddress.isSelected = false
            binding.tvStartWorkTime.isSelected = true
            binding.tvOffWorkTime.isSelected = false
            //设置时间
            val hourMinute = viewModel.getStartWorkTime()
            showTimePickerDialog(hourMinute.hour, hourMinute.minute, true)
        }
        binding.tvOffWorkTime.setOnClickListener {
            binding.tvAddress.isSelected = false
            binding.tvStartWorkTime.isSelected = false
            binding.tvOffWorkTime.isSelected = true
            //设置时间
            val hourMinute = viewModel.getOffWorkTime()
            showTimePickerDialog(hourMinute.hour, hourMinute.minute, false)
        }

        binding.btnSave.setOnClickListener {
            //保存
            viewModel.save()
        }

        viewModel.workAddressOptions().observe(this) {
            showSelectAddressDialog(it)
        }

        viewModel.workAddress().observe(this) {
            binding.tvAddress.text = it.detail
        }

        viewModel.startWorkTime().observe(this) {
            binding.tvStartWorkTime.text = it.toString()
        }

        viewModel.offWorkTime().observe(this) {
            binding.tvOffWorkTime.text = it.toString()
        }

        viewModel.error().observe(this) {
            Snackbar.make(binding.clContainer, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.saveResult().observe(this) {
            Snackbar.make(binding.clContainer, "保存成功", Snackbar.LENGTH_LONG).show()
            binding.root.postDelayed({
                finish()
            }, 2000)
        }
    }

    private fun showLocationPermissionRationale(permissions: Array<String>) {
        val rationaleDialog = AlertDialog.Builder(this)
            .setTitle(R.string.tips)
            .setMessage(R.string.location_permission_rationale_1)
            .setPositiveButton(R.string.agree, object : OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    requestLocationLauncher.launch(permissions)
                    dialog?.dismiss()
                }

            }).setNegativeButton(R.string.deny, object : OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }

            })
            .create()
        rationaleDialog.show()
    }

    private fun showSelectAddressDialog(addresses: List<Address>) {
        val selectAddressDialog = AlertDialog.Builder(this)
            .setAdapter(
                AddressAdapter(this, addresses)
            ) { dialog, which ->
                viewModel.setWorkAddress(addresses[which])
            }
            .create();
        selectAddressDialog.show()
    }

    private fun showTimePickerDialog(initHour: Int, initMinute: Int, startWorkTime: Boolean) {
        if (timePickerDialog != null) {
            timePickerDialog!!.dismiss()
        }
        timePickerDialog = TimePickerDialog(this,
            { view, hourOfDay, minute ->
                if (startWorkTime) {
                    viewModel.setStartWorkTime(hourOfDay, minute)
                } else {
                    viewModel.setOffWorkTime(hourOfDay, minute)
                }
            }, initHour, initMinute, true)
        if (!timePickerDialog!!.isShowing) {
            timePickerDialog!!.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timePickerDialog != null) {
            timePickerDialog!!.dismiss()
            timePickerDialog = null
        }
    }
}