class MemoryDataStore {
    private var booleans = [String : Bool]()
    private let onChange: (_ data: Dictionary<String, Any>) -> Void

    init(onChange: @escaping (_ data: Dictionary<String, Any>) -> Void) {
        self.onChange = onChange
    }

    func putBoolean(key: String, value: Bool) {
        booleans[key] = value
        onChange(booleans)
    }

    func getBoolean(key: String, defValue: Bool) -> Bool {
        return booleans[key] ?? defValue
    }
}
