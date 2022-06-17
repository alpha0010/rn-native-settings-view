class SettingsViewController: UITableViewController {
    private let dataStore: MemoryDataStore
    private let onDetails: (_ key: String) -> Void
    private var elements = [PreferenceElement]()
    private var nextTag = 1
    private var keyToTag = [String : Int]()
    private var tagToKey = [Int : String]()

    init(_ dataStore: MemoryDataStore, onDetails: @escaping (_ key: String) -> Void) {
        self.dataStore = dataStore
        self.onDetails = onDetails
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
                  let elData = confValue as? NSDictionary,
                  let type = elData["type"] as? String,
                  let weight = elData["weight"] as? NSNumber
            else {
                continue
            }
            switch type {
            case "details":
                if let details = elData["details"] as? String,
                   let title = elData["title"] as? String {
                    elements.append(DetailsElement(key: key, title: title, details: details, weight: weight.intValue))
                }
            case "switch":
                if let initial = elData["initialValue"] as? Bool,
                   let title = elData["title"] as? String {
                    dataStore.putBoolean(key: key, value: initial)
                    elements.append(SwitchElement(key: key, title: title, weight: weight.intValue))
                }
            default:
                continue
            }
        }
        elements.sort { $0.weight < $1.weight }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return elements.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: UITableViewCell
        let element = elements[indexPath.row]
        if let detailsElem = element as? DetailsElement {
            if let reuse = tableView.dequeueReusableCell(withIdentifier: "DetailsRow") {
                cell = reuse
            } else {
                cell = UITableViewCell(style: .value1, reuseIdentifier: "DetailsRow")
                cell.accessoryType = .disclosureIndicator
            }
            cell.textLabel?.text = detailsElem.title
            cell.detailTextLabel?.text = detailsElem.details
        } else if let swElem = element as? SwitchElement {
            if let reuse = tableView.dequeueReusableCell(withIdentifier: "SwitchRow") {
                cell = reuse
            } else {
                cell = UITableViewCell(style: .default, reuseIdentifier: "SwitchRow")
                let sw = UISwitch()
                sw.addTarget(self, action: #selector(onSwitchChange), for: .valueChanged)
                cell.accessoryView = sw
                cell.selectionStyle = .none
            }
            cell.textLabel?.text = swElem.title
            if let sw = cell.accessoryView as? UISwitch {
                sw.tag = getTagFor(key: swElem.key)
                sw.isOn = dataStore.getBoolean(key: swElem.key, defValue: false)
            }
        } else {
            // This case should not occur.
            cell = UITableViewCell()
        }
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        if let detailsElem = elements[indexPath.row] as? DetailsElement {
            onDetails(detailsElem.key)
        }
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
