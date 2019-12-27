//
//  CreateChallengeMenuView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct CreateChallengeMenuView: View {
    var games: [GameType]
    var answer: (GameType) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Create your own challenge and share it with your friends, family and co-workers. You can also hide a secret message which will get unveiled after solving the challenge.").font(.body).padding(.horizontal, 16).padding(.top, 8).multilineTextAlignment(.center)
                ForEach(games, id: \.name) { gameType in
                    Button(action: {self.answer(gameType)}) {
                        HStack {
                            Image(gameType.getImageResource())
                            Text(gameType.getName()).frame(minWidth: 0, maxWidth: 160)
                        }
                    }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
                }
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
            .navigationBarTitle("Create challenge")
        }.navigationViewStyle(StackNavigationViewStyle()) 
    }
}
