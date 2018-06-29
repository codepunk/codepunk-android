package com.codepunk.codepunk.util.plugin

/**
 * <p>
 * Class that manages and supplies plugins based on supplied criteria. This criteria may or may
 * not change over the lifecycle of the app; therefore it is good practice to always using the
 * {@link PluginManager#get(Object)} method each time you need to reference a plugin rather than
 * saving the returned value. Upon calling {@link PluginManager#get(Object)}, PluginManager
 * will automatically determine if a new plugin needs to be created based on the given criteria.
 * </p>
 * <p>
 * Plugins allow you to avoid multiple <code>if</code>/<code>else</code> or <code>switch</code>
 * statements to perform certain behaviors based on a set of criteria. This can range from logging
 * information only if running in a development environment, or performing a certain action only
 * if a user is logged in, showing extra controls if the user has admin rights, showing a
 * "what's new" dialog the first time the app is run after an upgrade, or showing a special screen
 * if it is a user's birthday.
 * </p>
 * <p>
 * As an example, suppose you wanted to show an encouraging message when someone used the app on a
 * Monday. You could easily include an <code>if</code> statement to accomplish this. But imagine
 * that you also wanted to change the background color on Mondays, and add an extra button that
 * was only visible on Mondays, or show a different message on Friday, and so on. Soon your code
 * would be littered with <code>if</code> statements testing the day of the week.
 * </p>
 * <p>
 * You could instead set up a Monday PluginManager:
 * <pre>
 *
 * interface DayPlugin {
 *     fun showGreeting(context: Context)
 * }
 *
 * class BaseDayPlugin: DayPlugin {
 *     override fun showGreeting(context: Context) {
 *         // No action
 *     }
 * }
 *
 * class MondayPlugin: DayPlugin {
 *     override fun showGreeting(context: Context) {
 *         Toast.makeText(
 *                 context,
 *                 "Looks like someone has a case of the Mondays!",
 *                 Toast.LENGTH_LONG)
 *                 .show()
 *     }
 * }
 *
 * class DayPluginManager: PluginManager<DayPlugin, Calendar>() {
 *     override fun isPluginStale(state: Calendar?): Boolean {
 *         return isMonday(activeState) != isMonday(state)
 *     }
 *
 * override fun newPlugin(state: Calendar?): DayPlugin {
 *         return if (isMonday(state)) MondayPlugin() else BaseDayPlugin()
 *     }
 *
 * private fun isMonday(calendar: Calendar?): Boolean {
 *         return calendar?.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
 *     }
 * }
 *
 * </pre>
 * </p>
 * <p>
 * In the above example, we created a <code>DayPlugin</code> with a single method,
 * <code>showGreeting</code>. In the example mentioned previously, we could have also
 * included a "<code>setBackgroundColor</code>" method, and so on. Since the current date/time
 * is constantly changing, we pass that along with each call to {@link PluginManager#get}.
 * PluginManager in turn calls {@link PluginManager#isPluginStale(Calendar)} and
 * {@link PluginManager#newPlugin(Calendar)} as appropriate to determine whether the existing
 * plugin (if any) is "stale" and a new one needs to be created.
 * </p>
 * <p>To make use of this new plugin, we can code something like the following in an Activity:
 * <pre>
 * override fun onStart() {
 *   super.onStart()
 *   DayPluginManager().get(Calendar.getInstance()).showGreeting(this)
 * }
 * </pre>
 * </p>
 * <p>
 * This is, of course, a rough example but it shows the power of using PluginManagers. Note that we
 * have avoided <code>if</code>/<code>else</code> or <code>switch</code> statements in our
 * <code>MainActivity</code> class, which makes for cleaner and more modular code. Another benefit
 * is that if you are testing a new feature, that feature can be implemented as a plugin. If
 * at any time you want to remove that feature entirely, it becomes much easier because it is
 * completely contained in its own plugin class and the calling class (in this case, MainActivity)
 * doesn't need to be modified, or even aware that the plugin was removed.
 * </p>
 * @param <Plugin> An interface or class that will be managed by this PluginManager.
 * @param <State> A class representing a state that may or may not change during the
 *                application lifecycle.
 */
@Suppress("UNUSED")
abstract class PluginManager<Plugin, State>(
        _activePlugin: Plugin? = null,
        _pluginListener: PluginListener<Plugin, State>? = null) {

    //region Nested classes

    /**
     * Interface that serves as a listener for when plugins are activated (i.e. created) and
     * deactivated (destroyed).
     * @param <Plugin> An interface or class that will be managed by this PluginManager.
     * @param <State> A class representing a state that may or may not change during the
     *                 application lifecycle.
     */
    interface PluginListener<Plugin, State> {
        fun onActivatePlugin(plugin: Plugin, state: State?)
        fun onDeactivatePlugin(plugin: Plugin)
    }

    //endregion Nested classes

    //region Fields

    /**
     * The state used to create the currently-active plugin.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var activeState: State? = null
        private set

    /**
     * The currently-active plugin (if any).
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var activePlugin: Plugin? = _activePlugin
        private set

    /**
     * Class that listens for when plugins are activated and deactivated.
     */
    private var pluginListener: PluginListener<Plugin, State>? = _pluginListener

    //endregion Fields

    //region Methods

    /**
     * Returns a plugin appropriate to the supplied <code>state</code>.
     * @param state The state used to create a new plugin.
     * @return The currently-active plugin.
     */
    fun get(state: State?): Plugin {
        if (activePlugin == null || isPluginStale(state)) {
            activePlugin?.let {
                onDeactivatePlugin(it)
                pluginListener?.onDeactivatePlugin(it)
            }
            val plugin = newPlugin(state)
            activePlugin = plugin
            activeState = state
            onActivatePlugin(plugin, state)
            pluginListener?.onActivatePlugin(plugin, state)
        }
        return activePlugin!!
    }

    //endregion Methods

    //region Protected methods

    /**
     * Method that determines whether the active plugin is "stale". That is, based on the supplied
     * <code>state</code>, whether a new plugin needs to be created or not.
     * @param state The state that help determine whether the active plugin is "stale" and a
     *               new plugin needs to be created.
     * @return Whether the supplied plugin is "stale" based on the given state.
     */
    protected abstract fun isPluginStale(state: State?): Boolean

    /**
     * Creates a new plugin based on the supplied <code>state</code>.
     * @param state The state to use to create the new plugin.
     * @return The new plugin.
     */
    protected abstract fun newPlugin(state: State?): Plugin

    /**
     * Called when a plugin is activated (i.e. created).
     * @param plugin The newly-active plugin.
     * @param state The state used to create the plugin.
     */
    @Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
    protected fun onActivatePlugin(plugin: Plugin, state: State?) {
        // No action
    }

    /**
     * Called when a plugin is about to be deactivated (i.e. destroyed).
     * @param plugin The plugin that is about to be deactivated.
     */
    @Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
    protected fun onDeactivatePlugin(plugin: Plugin) {
        // No action
    }

    //endregion Protected methods
}
