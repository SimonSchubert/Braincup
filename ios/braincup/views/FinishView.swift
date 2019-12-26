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
    var newHighscore: Bool
    var answeredAllCorrect: Bool
    var random: () -> Void
    var again: () -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Score: \(rank)").font(.title)
                if (answeredAllCorrect) {
                    Text("You got 1 extra point for making zero mistakes.").font(.subheadline).padding(.top, 8)
                }
                if(newHighscore) {
                    Text("New highscore").font(.subheadline).padding(.top, 8)
                }
                Button(action: {self.random()}) {
                    HStack {
                        Image("icons8-dice")
                        Text("Play random game")
                    }
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 16)
                Button(action: {self.again()}) {
                    HStack {
                        Image("icons8-reset")
                        Text("Play again")
                    }
           }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle()) 
    }
}

struct FinishView_Previews: PreviewProvider {
    static var previews: some View {
        FinishView(rank: "12", newHighscore: true, answeredAllCorrect: true, random: {}, again: {}, back: {})
    }
}
