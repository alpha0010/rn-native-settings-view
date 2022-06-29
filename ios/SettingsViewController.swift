class SettingsViewController: UITableViewController {
    private let dataStore: MemoryDataStore
    private let onDetails: (_ key: String) -> Void
    private var elements = [PreferenceElement]()
    private var nextTag = 1
    private var signature = ""
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

    func setConfig(_ config: NSDictionary) -> Bool {
        dataStore.ready = false
        var sig = "" // Not fully correct, but should be good enough.
        elements.removeAll()
        for (confKey, confValue) in config {
            guard let key = confKey as? String,
                  let elData = confValue as? NSDictionary,
                  let type = elData["type"] as? String,
                  let weight = (elData["weight"] as? NSNumber)?.intValue
            else {
                continue
            }
            let icon = getIcon(data: elData)
            switch type {
            case "details":
                if let details = elData["details"] as? String,
                   let title = elData["title"] as? String {
                    elements.append(DetailsElement(key: key, title: title, details: details, icon: icon, weight: weight))
                    sig += "\(key)-\(title)-"
                }
            case "list":
                // On iOS, list selection is split over screens. So, only
                // show the radio control if it is the only element.
                if let value = elData["value"] as? String,
                   let labels = (elData["labels"] as? NSArray)?.toStringArray(),
                   let title = elData["title"] as? String,
                   let values = (elData["values"] as? NSArray)?.toStringArray(),
                   labels.count == values.count {
                    dataStore.putString(key: key, value: value)
                    if config.count == 1 {
                        for (idx, rowData) in zip(labels, values).enumerated() {
                            elements.append(RadioElement(key: key, title: rowData.0, rowKey: rowData.1, icon: nil, weight: idx))
                            sig += "\(key)-\(rowData.0)-\(rowData.1)-"
                        }
                    } else {
                        // Press event expects client to push screen.
                        var details = ""
                        if let idx = values.firstIndex(of: value) {
                            details = labels[idx]
                        }
                        elements.append(DetailsElement(key: key, title: title, details: details, icon: icon, weight: weight))
                        sig += "\(key)-\(title)-"
                    }
                }
            case "switch":
                if let value = elData["value"] as? Bool,
                   let title = elData["title"] as? String {
                    dataStore.putBoolean(key: key, value: value)
                    elements.append(SwitchElement(key: key, title: title, icon: icon, weight: weight))
                    sig += "\(key)-\(title)-"
                }
            default:
                continue
            }
        }
        elements.sort { $0.weight < $1.weight }
        dataStore.ready = true
        let structureDidChange = sig != signature
        signature = sig
        return structureDidChange
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
            cell.imageView?.image = detailsElem.icon
            cell.textLabel?.text = detailsElem.title
            cell.detailTextLabel?.text = detailsElem.details
        } else if let radioElem = element as? RadioElement {
            if let reuse = tableView.dequeueReusableCell(withIdentifier: "RadioRow") {
                cell = reuse
            } else {
                cell = UITableViewCell(style: .default, reuseIdentifier: "RadioRow")
            }
            cell.accessoryType = dataStore.getString(key: radioElem.key, defValue: "") == radioElem.rowKey ? .checkmark : .none
            cell.imageView?.image = radioElem.icon
            cell.textLabel?.text = radioElem.title
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
            cell.imageView?.image = swElem.icon
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
        tableView.deselectRow(at: indexPath, animated: true)
        if let detailsElem = elements[indexPath.row] as? DetailsElement {
            onDetails(detailsElem.key)
        } else if let radioElem = elements[indexPath.row] as? RadioElement {
            dataStore.putString(key: radioElem.key, value: radioElem.rowKey)
            for (idx, element) in elements.enumerated() {
                guard let row = element as? RadioElement,
                      let cell = tableView.cellForRow(at: IndexPath(row: idx, section: indexPath.section)) else {
                    continue
                }
                cell.accessoryType = radioElem.rowKey == row.rowKey ? .checkmark : .none
            }
        }
    }

    @objc func onSwitchChange(sw: UISwitch) {
        if let key = tagToKey[sw.tag] {
            dataStore.putBoolean(key: key, value: sw.isOn)
        }
    }

    func notifyDataChanged() {
        dataStore.ready = false
        for (idx, element) in elements.enumerated() {
            guard let cell = tableView.cellForRow(at: IndexPath(row: idx, section: 0)) else {
                continue
            }
            if let detailsElem = element as? DetailsElement {
                // This element may be the summary for a list selector.
                cell.detailTextLabel?.text = detailsElem.details
            } else if let radioElem = element as? RadioElement {
                cell.accessoryType = dataStore.getString(key: radioElem.key, defValue: "") == radioElem.rowKey ? .checkmark : .none
            } else if let swElem = element as? SwitchElement,
                      let sw = cell.accessoryView as? UISwitch {
                sw.setOn(
                    dataStore.getBoolean(key: swElem.key, defValue: sw.isOn),
                    animated: true
                )
            }
        }
        dataStore.ready = true
    }

    private func getIcon(data: NSDictionary) -> UIImage? {
        guard let icnData = data["icon"] as? NSDictionary,
              let codepoint = (icnData["char"] as? NSNumber)?.intValue,
              let charCode = UnicodeScalar(codepoint),
              let fontName = icnData["font"] as? String,
              let font = UIFont(name: fontName, size: 26) else {
            return nil
        }
        let size = 30
        UIGraphicsBeginImageContextWithOptions(CGSize(width: size, height: size), false, 0.0)
        guard let context = UIGraphicsGetCurrentContext() else {
            UIGraphicsEndImageContext()
            return nil
        }

        context.saveGState()

        context.addPath(UIBezierPath(roundedRect: CGRect(x: 0, y: 0, width: size, height: size), cornerRadius: 6).cgPath)
        UIColor.gray.setFill()
        context.closePath()
        context.fillPath()

        let iconStr = NSAttributedString(string: String(charCode), attributes: [.font: font, .foregroundColor: UIColor.white])
        let iconBounds = iconStr.size()
        iconStr.draw(at: CGPoint(x: (size - Int(iconBounds.width)) / 2, y: (size - Int(iconBounds.height)) / 2))

        let rendered = UIGraphicsGetImageFromCurrentImageContext()
        context.restoreGState()
        UIGraphicsEndImageContext()
        return rendered
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

extension NSArray {
    func toStringArray() -> [String] {
        var res: [String] = []
        for item in self {
            if let str = item as? String {
                res.append(str)
            }
        }
        return res
    }
}
