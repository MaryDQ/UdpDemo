package com.mlx.udpdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private var localPort = 9012
    private var remotePort = 9012
    private var remoteAddress: InetAddress? = null
    private var socketUdp: DatagramSocket? = null
    private var socketSendPkg: DatagramPacket? = null
    private var socketRecPkg: DatagramPacket? = null
    private var content = "zlg is a sexy man !"
    private var isStoping = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            try {
                remoteAddress= InetAddress.getByName(editText.text.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            remotePort=editText2.text.toString().toInt()
            localPort=editText3.text.toString().toInt()
            testSendUdp()
        }

        button2.setOnClickListener {
            isStoping = true
        }


    }

    private fun receiveUdpData() {
        val job = runBlocking {
            launch {
                var recBuffer = ByteArray(1024)
                socketRecPkg = DatagramPacket(recBuffer, recBuffer.size)
                while (!isStoping) {
                    socketUdp?.receive(socketRecPkg)
                    val result = String(socketRecPkg!!.data, 0, socketRecPkg!!.data.size)
                    if (result.isNotEmpty()) {
                        Toast.makeText(applicationContext, "收到的信息:$result", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    private fun testSendUdp() {
        runBlocking {
            launch {
                try {
                    socketUdp = DatagramSocket(localPort)
                    val byteDate = content.toByteArray()
                    socketSendPkg = DatagramPacket(byteDate, byteDate.size, remoteAddress, remotePort)
                    socketUdp?.send(socketSendPkg)
                    receiveUdpData()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    socketUdp?.close()
                }
            }
        }
    }
}
