struct SwitchElement {
    let key: String
    let title: String
    let weight: Int
}

class SettingsViewController: UITableViewController {
    private let dataStore: MemoryDataStore
    private var elements = [SwitchElement]()
    private var nextTag = 1
    private var keyToTag = [String : Int]()
    private var tagToKey = [Int : String]()

    init(_ dataStore: MemoryDataStore) {
        self.dataStore = dataStore
        if #available(iOS 13.0, *) {
            super.init(style: .insetGrouped)
        } else {
            super.init(style: .grouped)
        }
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setConfig(_ config: NSDictionary) {
        elements.removeAll()
        for (confKey, confValue) in config {
            guard let key = confKey as? String,
                  let swData = confValue as? NSDictionary,
                  let title = swData["title"] as? String,
                  let initial = swData["initialValue"] as? Bool,
                  let weight = swData["weight"] as? NSNumber
            else {
                continue
            }
            dataStore.putBoolean(key: key, value: initial)
            elements.append(SwitchElement(key: key, title: title, weight: weight.intValue))
        }
        elements.sort { $0.weight < $1.weight }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.allowsSelection = false
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return elements.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let element = elements[indexPath.row]
        let cell: UITableViewCell
        if let reuse = tableView.dequeueReusableCell(withIdentifier: "SwitchRow") {
            cell = reuse
        } else {
            cell = UITableViewCell(style: .default, reuseIdentifier: "SwitchRow")
            let sw = UISwitch()
            sw.addTarget(self, action: #selector(onSwitchChange), for: .valueChanged)
            cell.accessoryView = sw
        }
        cell.textLabel?.text = element.title
        if let sw = cell.accessoryView as? UISwitch {
            sw.tag = getTagFor(key: element.key)
            sw.isOn = dataStore.getBoolean(key: element.key, defValue: false)
        }
        return cell
    }

    @objc func onSwitchChange(sw: UISwitch) {
        if let key = tagToKey[sw.tag] {
            dataStore.putBoolean(key: key, value: sw.isOn)
        }
    }

    private func getTagFor(key: String) -> Int {
        if let tag = keyToTag[key] {
            return tag
        }
        let tag = nextTag
        nextTag += 1
        keyToTag[key] = tag
        tagToKey[tag] = key
        return tag
    }
}
