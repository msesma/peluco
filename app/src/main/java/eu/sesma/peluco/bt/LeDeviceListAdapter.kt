package eu.sesma.peluco.bt

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import eu.sesma.peluco.R


class LeDeviceListAdapter(val listener: ClickListener) : RecyclerView.Adapter<LeViewHolder>() {

    private val leDevices: MutableList<BluetoothDevice> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LeViewHolder(
                inflater.inflate(R.layout.listitem_device, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return leDevices.size
    }

    override fun onBindViewHolder(holder: LeViewHolder, position: Int) {
        holder.bind(leDevices[position])
    }

    override fun getItemId(position: Int) = position.toLong()

    fun addDevice(device: BluetoothDevice) {
        if (!leDevices.contains(device)) leDevices.add(device)
    }

    fun getDevice(position: Int) = leDevices[position]

    fun clear() {
        leDevices.clear()
    }
}