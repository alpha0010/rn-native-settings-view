class SettingsViewController: UITableViewController {
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
            cell.accessoryView = UISwitch()
        }
        cell.textLabel?.text = "Sample toggle switch"
        return cell
    }
}
