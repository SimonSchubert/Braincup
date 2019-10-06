//
//  MainMenuView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct MainMenuView: View {
    var title: String
    var description: String
    var games: [GameType]
    var callback: (GameType) -> Void
    
    var body: some View {
        VStack {
            Text(title).font(.title)
            Text(description).font(.body).padding(.horizontal, 16).padding(.bottom, 16).multilineTextAlignment(.center)
            ForEach(games, id: \.name) { gameType in
                Button(action: {self.callback(gameType)}) {
                    Text(gameType.getName())
                }.buttonStyle(BackgroundButtonStyle())
            }
            Image("waiting")
        }
    }
}
