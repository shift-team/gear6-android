package com.shift.gear6

import br.ufrn.imd.obd.commands.IObdCommand
import br.ufrn.imd.obd.commands.ObdCommand
import br.ufrn.imd.obd.commands.engine.AbsoluteLoadCommand
import br.ufrn.imd.obd.commands.engine.RPMCommand

class CommandBinding {
    var createCommand: (()-> ObdCommand)? = null

    companion object {
        @JvmStatic
        fun createBindings(): HashMap<String, CommandBinding> {
            val bindings = HashMap<String, CommandBinding>()

            val rpmBinding = CommandBinding()
            rpmBinding.createCommand = { RPMCommand() }

            bindings[CommandNames.RPM] = rpmBinding

            val absoluteLoadBinding = CommandBinding()
            absoluteLoadBinding.createCommand = { AbsoluteLoadCommand() }

            bindings[CommandNames.AbsoluteLoad] = absoluteLoadBinding

            return bindings
        }

        @JvmStatic
        var bindings = createBindings()
    }
}