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
    var instructions: (GameType) -> Void
    var score: (GameType) -> Void

    var storage = UserStorage()
    
    var body: some View {
        ScrollView {
            VStack {
                Spacer()
                Text(title).font(.title).padding(.top, 16)
                Text(description).font(.body).padding(.horizontal, 16).padding(.bottom, 16).multilineTextAlignment(.center)
                ForEach(games, id: \.name) { gameType in
                    HStack {
                        Button(action: {self.instructions(gameType)}) {
                            HStack {
                                Image(self.getImageResource(game: gameType))
                                Text(gameType.getName()).frame(minWidth: 0, maxWidth: 160)
                            }
                        }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
                        if(self.storage.getHighScore(gameId: gameType.getId()) > 0) {
                            Button(action: {self.score(gameType)}) {
                                HStack {
                                    Image(gameType.getMedalResource(score: self.storage.getHighScore(gameId: gameType.getId())))
                                    Text("\(self.storage.getHighScore(gameId: gameType.getId()))").frame(minWidth: 15)
                                }
                            }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
                        }
                    }
                }
                Image("waiting")
                Spacer()
            }.frame(minHeight: UIScreen.main.bounds.height)
        }
    }
    
    func getImageResource(game: GameType) -> String {
        switch game {
            case GameType.sherlockCalculation:
                return "icons8-search"
            case GameType.colorConfusion:
                return "icons8-fill_color"
            case GameType.chainCalculation:
                return "icons8-edit_link"
            case GameType.mentalCalculation:
                return "icons8-math"
            case GameType.heightComparison:
                return "icons8-height"
            case GameType.fractionCalculation:
                return "icons8-divide"
            default:
                return ""
        }
    }
}
