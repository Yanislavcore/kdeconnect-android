package org.kde.kdeconnect.Plugins.UnlockPlugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.json.JSONException;
import org.kde.kdeconnect.Plugins.Plugin;
import org.kde.kdeconnect.Plugins.RunCommandPlugin.CommandEntry;
import org.kde.kdeconnect.Plugins.RunCommandPlugin.RunCommandPlugin;

public class UnlockPlugin extends Plugin {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final RunCommandPlugin plugin = device.getPlugin(RunCommandPlugin.class);
            plugin.getCommandList()
                    .stream()
                    .map(it -> {
                        try {
                            return new CommandEntry(it.getString("name"), it.getString("command"), it.getString("key"));
                        } catch (JSONException e) {
                            Log.e("UnlockPlugin", "Can't parse command", e);
                            return null;
                        }
                    })
                    .filter(it -> it != null && it.getName().equalsIgnoreCase("Unlock"))
                    .findFirst()
                    .ifPresent(it -> plugin.runCommand(it.getKey()));
        }
    };

    @Override
    public String getDisplayName() {
        return "Unlock plugin";
    }

    @Override
    public String getDescription() {
        return "Unlocks desktop on phones unlock";
    }

    @Override
    public String[] getSupportedPacketTypes() {
        return new String[]{};
    }

    @Override
    public String[] getOutgoingPacketTypes() {
        return new String[]{};
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public boolean onCreate() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        filter.setPriority(500);
        context.registerReceiver(receiver, filter);

        return true;
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(receiver);
    }
}
