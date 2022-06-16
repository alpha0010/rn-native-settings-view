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
    @objc var config: String = "" {
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
            if self.settingsController == nil {
                let controller = SettingsViewController(MemoryDataStore(
                    onChange: { data in
                        if let dispatch = self.onChange {
                            dispatch(["data" : data])
                        }
                    }
                ))
                self.reactAddController(toClosestParent: controller)
                self.addSubview(controller.view)
                self.settingsController = controller
            }
        }
    }
}
