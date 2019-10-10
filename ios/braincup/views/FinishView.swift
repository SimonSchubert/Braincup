//
//  FinishView.swift
//  braincup
//
//  Created by Simon Schubert on 07.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct FinishView: View {
    var rank: String
    var random: () -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Score: \(rank)")
                Button(action: {self.random()}) {
                    Text("Random game")
                }.buttonStyle(BackgroundButtonStyle())
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back")})
        }
    }
}
