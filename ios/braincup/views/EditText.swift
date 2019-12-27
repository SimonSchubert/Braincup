//
//  EditText.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI

struct EditText: View {
    var title: String
    var helperText: String
    var onChange: (String) -> Void
    @State private var value: String = ""
    
    var body: some View {
        VStack {
            Text(title).font(.system(size: CGFloat(18))).padding(.top, 32)
            TextField("", text: $value, onEditingChanged: {v in self.onChange(self.value) }).textFieldStyle(RoundedBorderTextFieldStyle())
            Text(helperText).font(.footnote).multilineTextAlignment(.center)
        }
    }
}

struct EditText_Previews: PreviewProvider {
    
    static var previews: some View {
        EditText(title: "Title", helperText: "Title of the challenge. (optional)", onChange: {_ in })
    }
}
