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
    
    var body: some View {
        VStack {
            Text("Score: \(rank)")
            Button(action: {self.random()}) {
                Text("Random game")
            }.buttonStyle(BackgroundButtonStyle())
        }
    }
}
