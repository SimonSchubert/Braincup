//
//  ContentView.swift
//  braincup
//
//  Created by Simon Schubert on 03.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        VStack {
            Button(action: {}) {
                Text("Play")
            }
            Button(action: {}) {
                Text("Play 2")
            }
            Text("Hello World")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
