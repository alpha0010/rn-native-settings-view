protocol PreferenceElement {
    var key: String { get }
    var icon: UIImage? { get }
    var weight: Int { get }
}

struct DetailsElement: PreferenceElement {
    let key: String
    let title: String
    let details: String
    let icon: UIImage?
    let weight: Int
}

struct RadioElement: PreferenceElement {
    let key: String
    let title: String
    let rowKey: String
    let icon: UIImage?
    let weight: Int
}

struct SwitchElement: PreferenceElement {
    let key: String
    let title: String
    let icon: UIImage?
    let weight: Int
}
