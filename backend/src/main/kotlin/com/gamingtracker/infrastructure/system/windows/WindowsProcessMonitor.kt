package com.gamingtracker.infrastructure.system.windows

import com.gamingtracker.application.ports.output.ProcessMonitor
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT

class WindowsProcessMonitor : ProcessMonitor {
    override fun getRunningExecutables(): List<String> {
        val executables = mutableListOf<String>()
        val snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, WinDef.DWORD(0))
        if (snapshot == WinNT.INVALID_HANDLE_VALUE) {
            return emptyList()
        }

        try {
            val processEntry = Tlhelp32.PROCESSENTRY32.ByReference()
            if (Kernel32.INSTANCE.Process32First(snapshot, processEntry)) {
                do {
                    val exeFile = String(processEntry.szExeFile).trim { it <= ' ' || it == '\u0000' }
                    executables.add(exeFile)
                } while (Kernel32.INSTANCE.Process32Next(snapshot, processEntry))
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(snapshot)
        }
        
        return executables
    }
}
