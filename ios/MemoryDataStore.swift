class MemoryDataStore {
    private var booleans = [String : Bool]()
    private var strings = [String : String]()
    private let onChange: (_ data: Dictionary<String, Any>) -> Void

    init(onChange: @escaping (_ data: Dictionary<String, Any>) -> Void) {
        self.onChange = onChange
    }

    func putString(key: String, value: String) {
        strings[key] = value
        dispatchUpdate()
    }

    func putBoolean(key: String, value: Bool) {
        booleans[key] = value
        dispatchUpdate()
    }

    func getString(key: String, defValue: String) -> String {
        return strings[key] ?? defValue
    }

    func getBoolean(key: String, defValue: Bool) -> Bool {
        return booleans[key] ?? defValue
    }

    private func dispatchUpdate() {
        onChange(
            (booleans as [String : Any])
                .merging(strings) { (current, _) in current }
        )
    }
}
