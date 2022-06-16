@objc(RnNativeSettingsViewManager)
class RnNativeSettingsViewManager: RCTViewManager {
    @objc
    override static func requiresMainQueueSetup() -> Bool {
        return false
    }

    override func view() -> (RnNativeSettingsView) {
        return RnNativeSettingsView()
    }
}

class RnNativeSettingsView : UIView {
    @objc var config: NSDictionary = [:] {
        didSet {
            initViewControllerIfNeeded()
        }
    }
    @objc var onChange: RCTBubblingEventBlock?
    var settingsController: SettingsViewController?

    override func layoutSubviews() {
        super.layoutSubviews()
        if let controller = settingsController {
            controller.view.frame = self.frame
        }
    }

    override func removeFromSuperview() {
        if let controller = settingsController {
            controller.willMove(toParent: nil)
            controller.view.removeFromSuperview()
            controller.removeFromParent()
            settingsController = nil
        }
        super.removeFromSuperview()
    }

    private func initViewControllerIfNeeded() {
        DispatchQueue.main.async {
            if let controller = self.settingsController {
                controller.setConfig(self.config)
                controller.tableView.reloadData()
            } else {
                let controller = SettingsViewController(MemoryDataStore(
                    onChange: { data in
                        if let dispatch = self.onChange {
                            dispatch(["data" : data])
                        }
                    }
                ))
                controller.setConfig(self.config)
                self.reactAddController(toClosestParent: controller)
                self.addSubview(controller.view)
                self.settingsController = controller
            }
        }
    }
}
