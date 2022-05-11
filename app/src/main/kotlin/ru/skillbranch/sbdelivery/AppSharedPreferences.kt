package ru.skillbranch.sbdelivery

import android.content.Context
import ru.skillbranch.common.ASharedPreferences
import ru.skillbranch.common.ISharedPreferences
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.extension.manage

public interface IAppSharedPreferences : ISharedPreferences {
    // Use RAM for database (Read only for this application)
    public val dbIsRAM: Boolean

    // Token of refresh access token
    public var refreshToken: String

    // Access token
    public var accessToken: String

    // Access token
    public var userId: String

    // userFirstName
    public var userFirstName: String

    // userLastName
    public var userLastName: String

    // userEmail
    public var userEmail: String

    // lastModDateDishes
    public var lastModDateDishes: Long

    // lastModDateCategory
    public var lastModDateCategory: Long

    // Use useMyLocation
    public var useMyLocation: Boolean

    // Order data
    public var address: String
    public var entrance: String
    public var floor: String
    public var apartment: String
    public var intercom: String
    public var comment: String
}

public class AppSharedPreferences(ctx: Context) : ASharedPreferences(ctx), IAppSharedPreferences {

    // Use RAM for database (Read only for this application)
    override val dbIsRAM: Boolean by store.manage(false)

    // Token of refresh access token
    override var refreshToken: String by store.manage("")

    // Access token
    override var accessToken: String by store.manage("")

    // Access token
    override var userId: String by store.manage("")

    // userFirstName
    override var userFirstName: String by store.manage("")

    // userLastName
    override var userLastName: String by store.manage("")

    // userEmail
    override var userEmail: String by store.manage("")

    // lastModDateDishes
    override var lastModDateDishes: Long by store.manage(0)

    // lastModDateCategory
    override var lastModDateCategory: Long by store.manage(0)

    // Use useMyLocation
    override var useMyLocation: Boolean by store.manage(false)

    // Order data
    public override var address: String by store.manage("")
    public override var entrance: String by store.manage("")
    public override var floor: String by store.manage("")
    public override var apartment: String by store.manage("")
    public override var intercom: String by store.manage("")
    public override var comment: String by store.manage("")

    init {
        // Initializing other settings
        logd("Inited shared property dbIsRAM = $dbIsRAM")
        logd("Inited shared property refreshToken = $refreshToken")
        logd("Inited shared property accessToken = $accessToken")
        logd("Inited shared property userId = $userId")
        logd("Inited shared property userFirstName = $userFirstName")
        logd("Inited shared property userLastName = $userLastName")
        logd("Inited shared property userEmail = $userEmail")
        logd("Inited shared property lastModDateDishes = $lastModDateDishes")
        logd("Inited shared property lastModDateCategory = $lastModDateCategory")
        logd("Inited shared property useMyLocation = $useMyLocation")
        logd("Inited shared property address = $address")
        logd("Inited shared property entrance = $entrance")
        logd("Inited shared property floor = $floor")
        logd("Inited shared property room = $apartment")
        logd("Inited shared property intercom = $intercom")
        logd("Inited shared property comment = $comment")

        /*changesListener.apply {
            add { k, v -> logd("add OnSharedPreferenceChangeListener for key = $k value=$v") }
        }*/
    }

    override fun migratePreferences() {
        TODO("Not yet implemented")
    }
}
