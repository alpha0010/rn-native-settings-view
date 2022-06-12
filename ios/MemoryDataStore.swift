class MemoryDataStore {
    private let onChange: (_ data: Dictionary<String, Any>) -> Void

    init(onChange: @escaping (_ data: Dictionary<String, Any>) -> Void) {
        self.onChange = onChange
    }

    func putBoolean(key: String, value: Bool) {
        onChange([key: value])
    }
}
