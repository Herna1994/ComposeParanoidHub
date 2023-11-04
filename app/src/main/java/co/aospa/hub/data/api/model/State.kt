package co.aospa.hub.data.api.model

open class State {
    object NONE: State()
    object NO_NETWORK: State()
    object CAN_SEARCH: State()
    object SEARCHING : State()
    object READY_TO_INSTALL : State()
    object DOWNLOADING : State()
    object INSTALLING : State()
}