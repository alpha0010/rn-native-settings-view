class SettingsViewController: UITableViewController {
    private let dataStore: MemoryDataStore

    init(_ dataStore: MemoryDataStore) {
        self.dataStore = dataStore
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.allowsSelection = false
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: UITableViewCell
        if let reuse = tableView.dequeueReusableCell(withIdentifier: "SwitchRow") {
            cell = reuse
        } else {
            cell = UITableViewCell(style: .default, reuseIdentifier: "SwitchRow")
            let sw = UISwitch()
            sw.addTarget(self, action: #selector(onSwitchChange), for: .valueChanged)
            cell.accessoryView = sw
        }
        cell.textLabel?.text = "Sample toggle switch"
        return cell
    }

    @objc func onSwitchChange(sw: UISwitch) {
        dataStore.putBoolean(key: "switch", value: sw.isOn)
    }
}
